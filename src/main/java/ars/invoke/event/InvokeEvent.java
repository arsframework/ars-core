package ars.invoke.event;

import java.util.Date;
import java.util.EventObject;

import ars.invoke.request.Requester;

/**
 * 请求调用事件
 *
 * @author wuyongqiang
 */
public abstract class InvokeEvent extends EventObject {
    private static final long serialVersionUID = 1L;

    private Date timestamp = new Date(); // 时间戳

    public InvokeEvent(Requester requester) {
        super(requester);
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public Requester getSource() {
        return (Requester) super.getSource();
    }

}
