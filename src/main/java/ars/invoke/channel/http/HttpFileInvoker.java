package ars.invoke.channel.http;

import java.io.File;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import ars.util.Streams;
import ars.invoke.remote.Remotes;
import ars.invoke.remote.Endpoint;
import ars.invoke.request.Requester;
import ars.invoke.channel.http.AbstractHttpInvoker;

/**
 * 基于文件流的Http远程调用实现
 * 
 * @author yongqiangwu
 *
 */
public class HttpFileInvoker extends AbstractHttpInvoker {

	@Override
	protected Object response(Requester requester, Endpoint endpoint, HttpResponse response) throws Exception {
		HttpEntity entity = response.getEntity();
		File file = new File(Remotes.getDirectory(),
				new StringBuilder("download-").append(UUID.randomUUID()).append(".temp").toString());
		try {
			Streams.write(entity.getContent(), file);
			return file;
		} finally {
			EntityUtils.consumeQuietly(entity);
		}
	}

}
