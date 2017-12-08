package ars.util;

/**
 * 地图操作工具类
 * 
 * @author wuyq
 * 
 */
public final class Maps {
	/**
	 * 地球半径（米）
	 */
	public static double EARTH_RADIUS = 6378137;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 计算地球任意两个经纬度之间的距离（米）
	 * 
	 * @param longitude1
	 *            第一点经度
	 * @param latitude1
	 *            第一点维度
	 * @param longitude2
	 *            第二点经度
	 * @param latitude2
	 *            第二点维度
	 * @return 距离（米）
	 */
	public static double getDistance(double longitude1, double latitude1,
			double longitude2, double latitude2) {
		latitude1 = rad(latitude1);
		latitude2 = rad(latitude2);
		double sa = Math.sin((latitude1 - latitude2) / 2.0);
		double sb = Math.sin(rad(longitude1 - longitude2) / 2.0);
		double distance = 2
				* EARTH_RADIUS
				* Math.asin(Math.sqrt(sa * sa + Math.cos(latitude1)
						* Math.cos(latitude2) * sb * sb));
		return distance;
	}

}
