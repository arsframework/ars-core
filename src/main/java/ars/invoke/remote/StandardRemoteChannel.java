package ars.invoke.remote;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.ReadableByteChannel;
import java.util.Map;
import java.util.UUID;
import java.util.Locale;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Collection;
import java.util.regex.Pattern;

import ars.util.Nfile;
import ars.util.Jsons;
import ars.util.Streams;
import ars.util.Strings;
import ars.invoke.Context;
import ars.invoke.Invokes;
import ars.invoke.request.Requester;
import ars.invoke.remote.RemoteChannel;
import ars.invoke.remote.RemoteRequester;
import ars.invoke.remote.StandardRemoteRequester;
import ars.invoke.remote.slice.Itoken;
import ars.invoke.remote.slice.Istring;
import ars.invoke.remote.slice.Istream;
import ars.invoke.remote.slice._ResourceDisp;
import ars.invoke.remote.slice.AMD_Resource_invoke;
import ars.invoke.remote.slice.AMD_Resource_upload;
import ars.invoke.remote.slice.AMD_Resource_download;
import ars.server.timer.AbstractTimerServer;

/**
 * 基于ICE消息中间的远程调用通道标准实现
 * 
 * @author wuyq
 * 
 */
public class StandardRemoteChannel extends _ResourceDisp implements RemoteChannel {
	private static final long serialVersionUID = 1L;
	private static final Pattern UPLOAD_FILE_PATTERN = Pattern
			.compile("upload-[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}..+");

	private transient Context context;
	private transient Map<String, StreamReader> streams = new HashMap<String, StreamReader>();

	/**
	 * 数据流读取抽象类
	 * 
	 * 如果数据流超过10秒为被读取则自动关闭并销毁
	 * 
	 * @author wuyq
	 *
	 */
	abstract class StreamReader {
		protected final String id;
		protected final AbstractTimerServer timer;
		private boolean destroied;
		private volatile long timestamp = System.currentTimeMillis();

		public StreamReader(String id) {
			if (id == null) {
				throw new IllegalArgumentException("Illegal id:" + id);
			}
			this.id = id;
			this.timer = new AbstractTimerServer() {

				@Override
				protected void execute() throws Exception {
					if (System.currentTimeMillis() - timestamp > 10000) {
						StreamReader.this.destroy();
					}
				}

			};
			this.timer.setInterval(1);
			this.timer.start();
		}

		/**
		 * 读取数据流
		 * 
		 * @param index
		 *            开始位置
		 * @param length
		 *            数据长度
		 * @return 字节数组
		 * @throws IOException
		 */
		public abstract byte[] read(int index, int length) throws IOException;

		/**
		 * 关闭数据流
		 * 
		 * @throws IOException
		 */
		public abstract void close() throws IOException;

		/**
		 * 获取数据流并更新时间戳
		 * 
		 * @param index
		 *            开始位置
		 * @param length
		 *            数据长度
		 * @return 字节数组
		 * @throws IOException
		 */
		public byte[] fetch(int index, int length) throws IOException {
			this.timestamp = System.currentTimeMillis();
			return this.read(index, length);
		}

		/**
		 * 销毁流对象
		 * 
		 * @throws IOException
		 */
		public void destroy() throws IOException {
			if (!this.destroied) {
				synchronized (this) {
					if (!this.destroied) {
						this.destroied = true;
						try {
							this.close();
						} finally {
							timer.stop();
							streams.remove(id);
						}
					}
				}
			}
		}

	}

	/**
	 * 文件流读取实现
	 * 
	 * @author wuyq
	 *
	 */
	class FileStreamReader extends StreamReader {
		protected final long size;
		protected final InputStream stream;

		public FileStreamReader(String id, File file) throws IOException {
			super(id);
			if (file == null) {
				throw new IllegalArgumentException("Illegal file:" + file);
			}
			this.size = file.length();
			this.stream = new FileInputStream(file);
		}

		public FileStreamReader(String id, Nfile file) throws IOException {
			super(id);
			if (file == null) {
				throw new IllegalArgumentException("Illegal file:" + file);
			}
			this.size = file.getSize();
			this.stream = file.getInputStream();
		}

		@Override
		public byte[] read(int index, int length) throws IOException {
			long unread = this.size - index;
			boolean end = length >= unread;
			byte[] buffer = new byte[end ? (int) unread : length];
			try {
				this.stream.read(buffer);
				return buffer;
			} finally {
				if (end) {
					this.destroy();
				}
			}
		}

		@Override
		public void close() throws IOException {
			this.stream.close();
		}
	}

	/**
	 * 输入流读取实现
	 * 
	 * @author wuyq
	 *
	 */
	class InputStreamReader extends StreamReader {
		protected final InputStream stream;

		public InputStreamReader(String id, InputStream stream) {
			super(id);
			if (stream == null) {
				throw new IllegalArgumentException("Illegal stream:" + stream);
			}
			this.stream = stream;
		}

		@Override
		public byte[] read(int index, int length) throws IOException {
			try {
				return Streams.getBytes(this.stream);
			} finally {
				this.destroy();
			}
		}

		@Override
		public void close() throws IOException {
			this.stream.close();
		}

	}

	/**
	 * 字节数组读取实现
	 * 
	 * @author wuyq
	 *
	 */
	class ByteArrayStreamReader extends StreamReader {
		protected final byte[] bytes;

		public ByteArrayStreamReader(String id, byte[] bytes) {
			super(id);
			if (bytes == null) {
				throw new IllegalArgumentException("Illegal bytes:" + bytes);
			}
			this.bytes = bytes;
		}

		@Override
		public byte[] read(int index, int length) throws IOException {
			try {
				return this.bytes;
			} finally {
				this.destroy();
			}
		}

		@Override
		public void close() throws IOException {

		}

	}

	/**
	 * 字节通道读取实现
	 * 
	 * @author wuyq
	 *
	 */
	class ByteChannelStreamReader extends StreamReader {
		protected final ReadableByteChannel channel;

		public ByteChannelStreamReader(String id, ReadableByteChannel channel) {
			super(id);
			if (channel == null) {
				throw new IllegalArgumentException("Illegal channel:" + channel);
			}
			this.channel = channel;
		}

		@Override
		public byte[] read(int index, int length) throws IOException {
			try {
				return Streams.getBytes(this.channel);
			} finally {
				this.destroy();
			}
		}

		@Override
		public void close() throws IOException {
			this.channel.close();
		}

	}

	/**
	 * 根据请求参数JSON字符串获取请求参数键/值映射
	 * 
	 * @param parameter
	 *            JSON参数
	 * @return 参数键/值映射
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> getParameters(String parameter) {
		if (Strings.isEmpty(parameter)) {
			return new HashMap<String, Object>(0);
		}
		Map<String, Object> parameters = (Map<String, Object>) Jsons.parse(parameter);
		for (Entry<String, Object> entry : parameters.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof String && UPLOAD_FILE_PATTERN.matcher((String) value).matches()) {
				String name = (String) value;
				entry.setValue(
						new Nfile(name.substring(name.indexOf('.') + 1), new File(Remotes.getDirectory(), name)));
			} else if (value instanceof Object[]) {
				Object[] array = (Object[]) value;
				if (array.length > 0) {
					for (int i = 0; i < array.length; i++) {
						Object item = array[i];
						if (item instanceof String && UPLOAD_FILE_PATTERN.matcher((String) item).matches()) {
							String name = (String) item;
							array[i] = new Nfile(name.substring(name.indexOf('.') + 1),
									new File(Remotes.getDirectory(), name));
						}
					}
				}
			} else if (value instanceof Collection) {
				Collection<Object> collection = (Collection<Object>) value;
				if (!collection.isEmpty()) {
					Iterator<Object> iterator = collection.iterator();
					while (iterator.hasNext()) {
						Object item = iterator.next();
						if (item instanceof String && UPLOAD_FILE_PATTERN.matcher((String) item).matches()) {
							String name = (String) item;
							collection.add(new Nfile(name.substring(name.indexOf('.') + 1),
									new File(Remotes.getDirectory(), name)));
							iterator.remove();
						}
					}
				}
			}
		}
		return parameters;
	}

	/**
	 * 获取请求对象
	 * 
	 * @param client
	 *            客户标识
	 * @param atoken
	 *            请求令牌
	 * @param uri
	 *            资源地址
	 * @param parameters
	 *            请求参数
	 * @param context
	 *            ICE上下文
	 * @return 请求对象
	 */
	protected RemoteRequester getRequester(String client, Itoken itoken, String uri, String parameter,
			Ice.Current context) {
		String host = ((Ice.IPConnectionInfo) context.con.getInfo()).remoteAddress;
		return new StandardRemoteRequester(this, context, null, Locale.getDefault(), client, host,
				Remotes.itoken2token(itoken), uri == null ? Strings.EMPTY_STRING : uri.trim(),
				this.getParameters(parameter));
	}

	@Override
	public Context getContext() {
		return this.context;
	}

	@Override
	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public Object dispatch(Requester requester) throws Exception {
		return requester.execute();
	}

	@Override
	public String getIdentifier() {
		return Remotes.COMMON_CHANNEL_NAME;
	}

	@Override
	public void invoke_async(AMD_Resource_invoke __cb, String client, Itoken token, String uri, String parameter,
			Ice.Current __current) {
		try {
			Requester requester = this.getRequester(client, token, uri, parameter, __current);
			Invokes.setCurrentRequester(requester);
			Object value = this.dispatch(requester);
			if (value instanceof Exception) {
				__cb.ice_exception((Exception) value);
			} else if (value instanceof File) {
				File file = (File) value;
				String id = UUID.randomUUID().toString();
				this.streams.put(id, new FileStreamReader(id, file));
				__cb.ice_response(new Istream(id, file.getName(), file.length(), true));
			} else if (value instanceof Nfile) {
				Nfile file = (Nfile) value;
				String id = UUID.randomUUID().toString();
				this.streams.put(id, new FileStreamReader(id, file));
				__cb.ice_response(new Istream(id, file.getName(), file.getSize(), true));
			} else if (value instanceof byte[]) {
				byte[] bytes = (byte[]) value;
				String id = UUID.randomUUID().toString();
				this.streams.put(id, new ByteArrayStreamReader(id, bytes));
				__cb.ice_response(new Istream(id, null, bytes.length, false));
			} else if (value instanceof InputStream) {
				InputStream stream = (InputStream) value;
				String id = UUID.randomUUID().toString();
				this.streams.put(id, new InputStreamReader(id, stream));
				__cb.ice_response(new Istream(id, null, stream.available(), false));
			} else if (value instanceof ReadableByteChannel) {
				ReadableByteChannel channel = (ReadableByteChannel) value;
				String id = UUID.randomUUID().toString();
				this.streams.put(id, new ByteChannelStreamReader(id, channel));
				__cb.ice_response(new Istream(id, null, 0, false));
			} else {
				__cb.ice_response(new Istring(Jsons.format(value)));
			}
		} catch (Exception e) {
			__cb.ice_exception(e);
		}
	}

	@Override
	public void upload_async(AMD_Resource_upload __cb, String name, byte[] buffer, int length, Ice.Current __current) {
		try {
			File file = new File(Remotes.getDirectory(), name);
			OutputStream os = new FileOutputStream(file, true);
			try {
				os.write(buffer, 0, length);
			} finally {
				os.close();
			}
			__cb.ice_response();
		} catch (Exception e) {
			__cb.ice_exception(e);
		}
	}

	@Override
	public void download_async(AMD_Resource_download __cb, String id, int index, int length, Ice.Current __current) {
		try {
			StreamReader stream = this.streams.get(id);
			if (stream == null) {
				throw new RuntimeException("Stream does not exist:" + id);
			}
			__cb.ice_response(stream.fetch(index, length));
		} catch (Exception e) {
			__cb.ice_exception(e);
		}
	}

}
