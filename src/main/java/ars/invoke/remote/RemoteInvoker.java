package ars.invoke.remote;

import ars.invoke.Invoker;
import ars.invoke.Resource;
import ars.invoke.request.Requester;

/**
 * 基于ICE消息中间的远程调用实现
 *
 * @author wuyongqiang
 */
public class RemoteInvoker implements Invoker {

    @Override
    public Object execute(Requester requester, Resource resource) throws Exception {
        Endpoint endpoint = (Endpoint) resource;
        String uri = endpoint.getUri();
        try {
            return Remotes.invoke(Remotes.getProxy(endpoint.getNodes()), requester.getToken(),
                uri == null ? requester.getUri() : uri, requester.getParameters());
        } catch (Ice.UnknownException e) {
            throw new Exception(e.unknown);
        }
    }

}
