package ars.file.ftp;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFileFilter;

import ars.file.Describe;
import ars.file.query.Queries;
import ars.file.query.AbstractQuery;
import ars.file.query.Queries.Condition;

/**
 * FTP文件查询集合实现
 *
 * @author wuyongqiang
 */
public class FTPQuery extends AbstractQuery {
    protected final ClientFactory clientFactory;

    public FTPQuery(ClientFactory clientFactory) {
        this(clientFactory, "/");
    }

    public FTPQuery(ClientFactory clientFactory, String workingDirectory) {
        super(workingDirectory);
        if (clientFactory == null) {
            throw new IllegalArgumentException("ClientFactory must not be null");
        }
        this.clientFactory = clientFactory;
    }

    @Override
    protected List<Describe> execute(final String path, final boolean spread, final Condition... conditions) {
        final List<Describe> describes = new LinkedList<Describe>();
        FTPClient client = null;
        try {
            client = this.clientFactory.connect();
            client.listFiles(path == null ? this.workingDirectory : new File(this.workingDirectory, path).getPath(),
                new FTPFileFilter() {

                    @Override
                    public boolean accept(FTPFile file) {
                        Describe describe = new Describe();
                        describe.setPath(new File(path, file.getName()).getPath());
                        describe.setName(file.getName());
                        describe.setSize(file.getSize());
                        describe.setModified(file.getTimestamp().getTime());
                        describe.setDirectory(file.isDirectory());
                        if (Queries.isSatisfy(describe, conditions)) {
                            describes.add(describe);
                        }
                        if (spread && describe.isDirectory()) {
                            describes.addAll(execute(describe.getPath(), spread, conditions));
                        }
                        return false;
                    }

                });
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (client != null) {
                try {
                    this.clientFactory.disconnect(client);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return describes;
    }

}
