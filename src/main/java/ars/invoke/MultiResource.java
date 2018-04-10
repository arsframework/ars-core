package ars.invoke;

/**
 * 多资源包装资源实现
 *
 * @author wuyongqiang
 */
public class MultiResource implements Resource {
    private static final long serialVersionUID = 1L;

    private String[] resources; // 目标资源地址数组

    public MultiResource(String... resources) {
        if (resources == null || resources.length == 0) {
            throw new IllegalArgumentException("Resources must not be empty");
        }
        this.resources = resources;
    }

    public String[] getResources() {
        return resources;
    }

}
