package com.szmirren.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.Proxy.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.szmirren.common.Constant;
import com.szmirren.common.StrUtil;
import com.szmirren.entity.DownloadItem;
import com.szmirren.view.AlertUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;

public class IndexController extends BaseController {
	private Logger LOG = Logger.getLogger(this.getClass());
	/** 下载地址合集 */
	@FXML
	private TextArea txtUrls;
	/** 存放目录 */
	@FXML
	private TextField txtProjectPath;
	/** 代理的主机 */
	@FXML
	private TextField txtProxyHost;
	/** 代理的端口号 */
	@FXML
	private TextField txtProxyPort;
	/** 是否启用代理 */
	@FXML
	private CheckBox ckUseProxy;
	/** 下载的线程数量 */
	@FXML
	private TextField txtDownCount;
	/** 选择根目录按钮 */
	@FXML
	private Button btnSelectFile;
	/** 解析下载地址 */
	@FXML
	private Button btnDecodeUrls;
	/** 解析下载地址 */
	@FXML
	private Button btnStartDownload;
	/** 解析下载地址 */
	@FXML
	private TableView<DownloadItem> tblProperty;
	/** 存储信息table里面的所有属性 */
	private ObservableList<DownloadItem> tblPropertyValues;
	/** 下载进度合集 */
	private Map<String, Label> downloadProgMaps = new HashMap<>();
	/** 下载状态合集 */
	private Map<String, Label> downloadStateMaps = new HashMap<>();

	/** 下载的id的种子 */
	private AtomicLong downIdIndex = new AtomicLong(0L);
	/** 是否正在解析视频中 */
	private AtomicBoolean isDecodeingUrls = new AtomicBoolean();
	/** 是否正在下载中 */
	private AtomicBoolean isDownloading = new AtomicBoolean();
	/** 下载文件的队列 */
	private ConcurrentLinkedDeque<DownloadItem> downQueue = new ConcurrentLinkedDeque<DownloadItem>();

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOG.debug("初始化首页...");
		this.txtProjectPath.setText(System.getProperty("user.dir") + "/video");
		this.txtProxyHost.setText("127.0.0.1");
		this.txtProxyPort.setText("7890");
		this.ckUseProxy.setSelected(true);
		tblPropertyValues = FXCollections.observableArrayList();
		TableColumn<DownloadItem, String> tdColumnId = new TableColumn<>("id");
		tdColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		TableColumn<DownloadItem, String> tdColumnUrl = new TableColumn<>("下载路径");
		tdColumnUrl.setCellValueFactory(new PropertyValueFactory<>("urlLabel"));
		TableColumn<DownloadItem, String> tdColumnProg = new TableColumn<>("下载进度");
		tdColumnProg.setCellValueFactory(new PropertyValueFactory<>("prog"));
		TableColumn<DownloadItem, Label> tdColumnState = new TableColumn<>("下载状态");
		tdColumnState.setCellValueFactory(new PropertyValueFactory<>("state"));
		Label label = new Label("未加载数据");
		tblProperty.setPlaceholder(label);
		this.tblProperty.getColumns().addAll(tdColumnId, tdColumnUrl, tdColumnProg, tdColumnState);
		// 设置列的大小自适应
		tblProperty.setColumnResizePolicy(resize -> {
			double width = resize.getTable().getWidth();
			tdColumnId.setPrefWidth(width * 0.1);
			tdColumnUrl.setPrefWidth(width * 0.5);
			tdColumnProg.setPrefWidth(width * 0.2);
			tdColumnState.setPrefWidth(width * 0.2);
			return true;
		});
		tblProperty.setItems(tblPropertyValues);
		LOG.debug("初始化首页成功!");
	}

	/**
	 * 选择项目文件
	 * 
	 * @param event
	 */
	public void onSelectProjectPath(ActionEvent event) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File file = directoryChooser.showDialog(super.getPrimaryStage());
		if (file != null) {
			txtProjectPath.setText(file.getPath());
			LOG.debug("选择文件项目目录:" + file.getPath());
		}
	}

	/**
	 * 解析并初始化下载地址
	 * 
	 * @param event
	 */
	public void onDecodeAndLoadUrls(ActionEvent event) {
		LOG.debug("初始化下载地址");
		String text = txtUrls.getText();
		if (StrUtil.isNullOrEmpty(text)) {
			AlertUtil.showInfoAlert("请先输入视频地址!");
			return;
		}
		if (isDecodeingUrls.get()) {
			AlertUtil.showInfoAlert("视频地址正在解析中,请稍后...");
			return;
		}
		if (isDownloading.get()) {
			AlertUtil.showInfoAlert("视频正在下载中,请稍后...");
			return;
		}
		this.tblPropertyValues.clear();
		isDecodeingUrls.set(true);
		String[] split = text.trim().replace("https://", "■https://").replace("http://", "■http://").split("■");
		for (String url : split) {
			url = url.trim();
			if (StrUtil.isNullOrEmpty(url) || !url.startsWith("http")) {
				continue;
			}
			String id = downIdIndex.incrementAndGet() + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmSSS"));
			DownloadItem item = new DownloadItem();
			item.setId(id);
			item.setUrl(url);
			Label label = new Label(url);
			label.setOnMouseClicked(e -> {
				AlertUtil.showInfoAlert(label.getText());
			});
			item.setUrlLabel(label);
			Label prog = new Label(Constant.STATE_DOWN_WAIT_START);
			prog.setOnMouseClicked(e -> {
				AlertUtil.showInfoAlert(prog.getText());
			});
			item.setProg(prog);
			Label state = new Label(Constant.STATE_DOWN_WAIT_START);
			state.setOnMouseClicked(e -> {
				String output = txtProjectPath.getText();
				createOutputFolder(output);
				if (Objects.equals(Constant.STATE_DOWN_ERROR, state.getText())) {
					startDownload(item, output, 1);
				} else if (Objects.equals(Constant.STATE_DOWN_WAIT, state.getText())) {
					startDownload(item, output, 1);
				}
			});
			item.setState(state);
			this.downloadProgMaps.put(id, prog);
			this.downloadStateMaps.put(id, state);
			this.tblPropertyValues.add(item);
		}
		if (this.tblPropertyValues.isEmpty()) {
			AlertUtil.showInfoAlert("视频下载地址无效,请先添加视频下载地址!");
		}
		isDecodeingUrls.set(false);
	}

	/**
	 * 解析并初始化下载地址
	 * 
	 * @param event
	 */
	public void onStartDownload(ActionEvent event) {
		try {
			LOG.debug("开始下载");
			if (isDecodeingUrls.get()) {
				AlertUtil.showInfoAlert("视频地址正在解析中,请稍后...");
				return;
			}
			if (isDownloading.get()) {
				AlertUtil.showInfoAlert("视频正在下载中,请稍后...");
				return;
			}
			String output = txtProjectPath.getText();
			if (StrUtil.isNullOrEmpty(output)) {
				AlertUtil.showInfoAlert("请先输入或选择保存路径!");
				return;
			}
			if (this.tblPropertyValues.isEmpty()) {
				if (this.tblPropertyValues.isEmpty()) {
					AlertUtil.showInfoAlert("视频下载地址无效,请先添加视频下载地址!");
				}
			}
			this.downQueue.addAll(this.tblPropertyValues);
			createOutputFolder(output);
			startDownload(null, output, StrUtil.getInteger(txtDownCount.getText(), 10));
		} catch (Exception e) {
			AlertUtil.showErrorAlert("下载视频失败!");
			LOG.error("执行下载视频任务-->失败", e);
		}
	}

	/**
	 * 开始下载视频
	 * 
	 * @param di
	 *          如果不为空就添加到队列中
	 * @param output
	 *          输出文件夹
	 * @param maxsize
	 *          最大下载线程数
	 */
	private void startDownload(DownloadItem di, String output, int maxsize) {
		if (di != null) {
			this.downQueue.add(di);
		}
		try {
			int max;
			if (maxsize >= this.downQueue.size()) {
				max = this.downQueue.size();
			} else {
				max = maxsize;
			}
			for (int i = 0; i < max; i++) {
				DownloadItem item = downQueue.isEmpty() ? null : downQueue.pop();
				if (item == null) {
					continue;
				}
				String id = item.getId();
				String url = item.getUrl();
				Task<Void> task = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						updateMessage(Constant.STATE_DOWN_ING);
						try {
							URL http = new URL(url);
							HttpURLConnection conn;
							if (ckUseProxy.isSelected()) {
								String host = txtProxyHost.getText().trim();
								Integer port = StrUtil.getInteger(txtProxyPort.getText(), 7890);
								Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(host, port));
								LOG.debug("使用了代理模式["+proxy+"]进行请求...");
								conn = (HttpURLConnection) http.openConnection(proxy);
							} else {
								LOG.debug("未使用进行请求...");
								conn = (HttpURLConnection) http.openConnection();
							}

							conn.setConnectTimeout(30000);
							int code = conn.getResponseCode();
							if (code == 200) {
								int length = conn.getContentLength();
								File file = new File(output + "/" + id + ".mp4");
								if (file.exists()) {
									file.delete();
								} else {
									file.createNewFile();
								}
								try (BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
										FileOutputStream out = new FileOutputStream(file)) {
									byte b[] = new byte[4096];
									int len = 0;
									int sum = 0;
									while ((len = in.read(b)) != -1) {
										sum += len;
										updateProgress(sum, length);
										out.write(b, 0, len);
									}
									out.flush();
									updateMessage(Constant.STATE_DOWN_OK);
								} catch (Exception e) {
									LOG.error("执行下载文件" + url + "-->失败:", e);
									updateTitle(e.toString());
									updateMessage(Constant.STATE_DOWN_ERROR);
								}
							} else {
								LOG.error("执行下载文件" + url + "打开连接-->失败:" + conn.getResponseMessage());
								updateTitle(conn.getResponseMessage());
								updateMessage(Constant.STATE_DOWN_ERROR);
							}
						} catch (IOException e) {
							LOG.error("执行下载文件" + url + "-->失败:", e);
							updateTitle(e.toString());
							updateMessage(Constant.STATE_DOWN_ERROR);
						}
						return null;
					}
				};
				Label prog = downloadProgMaps.get(id);
				if (prog != null) {
					task.progressProperty().addListener(l -> {
						prog.setText(String.format("%.2f", task.getProgress() * 100));
					});
					task.titleProperty().addListener(l -> {
						prog.setText("点击查看异常:" + task.titleProperty().get());
					});
				}
				Label state = downloadStateMaps.get(id);
				if (state != null) {
					task.messageProperty().addListener(l -> {
						if (Objects.equals(Constant.STATE_DOWN_OK, task.getMessage())) {
							state.setTextFill(Color.GREEN);
							startDownload(di, output, maxsize);
						} else if (Objects.equals(Constant.STATE_DOWN_ERROR, task.getMessage())) {
							state.setTextFill(Color.RED);
							if (item.errorCountIAG() <= 10) {
								downQueue.addFirst(item);
								startDownload(di, output, 1);
							}
						} else if (Objects.equals(Constant.STATE_DOWN_ING, task.getMessage())) {
							state.setTextFill(Color.BLACK);
						}
						state.setText(task.getMessage());
					});
				}
				new Thread(task).start();
			}
		} catch (Exception e) {
			AlertUtil.showErrorAlert("下载视频失败!");
			LOG.error("执行下载视频任务-->失败", e);
		}
	}

	/**
	 * 创建文件夹
	 * 
	 * @param output
	 */
	private void createOutputFolder(String output) {
		File file = new File(output);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

}
