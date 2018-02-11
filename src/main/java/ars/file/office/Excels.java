package ars.file.office;

import java.util.List;
import java.util.Date;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Collection;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.lang.reflect.Field;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

import ars.util.Beans;
import ars.util.Dates;
import ars.util.Files;
import ars.util.Nfile;
import ars.util.Strings;

/**
 * Excel文件操作工具类
 * 
 * @author yongqiangwu
 * 
 */
public final class Excels {
	private Excels() {

	}

	/**
	 * Excel读对象接口
	 * 
	 * @author yongqiangwu
	 * 
	 * @param <T>
	 *            数据模型
	 */
	public static interface Reader<T> {
		/**
		 * 读取Excel数据行并转换成对象实体
		 * 
		 * @param row
		 *            数据行对象
		 * @param count
		 *            当前记录数（从1开始）
		 * @return 对象实体
		 */
		public T read(Row row, int count);

	}

	/**
	 * Excel写对象接口
	 * 
	 * @author yongqiangwu
	 * 
	 * @param <T>
	 *            数据模型
	 */
	public static interface Writer<T> {
		/**
		 * 将对象实体写入到Excel数据行
		 * 
		 * @param entity
		 *            对象实体
		 * @param row
		 *            数据行对象
		 * @param count
		 *            当前记录数（从1开始）
		 */
		public void write(T entity, Row row, int count);

	}

	/**
	 * 获取Excel文件工作薄
	 * 
	 * @return Excel文件工作薄
	 */
	public static Workbook getWorkbook() {
		return new HSSFWorkbook();
	}

	/**
	 * 获取Excel文件工作薄
	 * 
	 * @param file
	 *            Excel文件对象
	 * @return Excel文件工作薄
	 * @throws IOException
	 *             IO操作异常
	 */
	public static Workbook getWorkbook(File file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("Illegal file:" + file);
		}
		InputStream is = new FileInputStream(file);
		try {
			return Files.isSuffix(file.getName(), "xls") ? new HSSFWorkbook(is) : new XSSFWorkbook(is);
		} finally {
			is.close();
		}
	}

	/**
	 * 获取Excel文件工作薄
	 * 
	 * @param file
	 *            Excel文件对象
	 * @return Excel文件工作薄
	 * @throws IOException
	 *             IO操作异常
	 */
	public static Workbook getWorkbook(Nfile file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("Illegal file:" + file);
		}
		InputStream is = file.getInputStream();
		try {
			return Files.isSuffix(file.getName(), "xls") ? new HSSFWorkbook(is) : new XSSFWorkbook(is);
		} finally {
			is.close();
		}
	}

	/**
	 * 将Excel数据写入文件
	 * 
	 * @param workbook
	 *            Excel文件工作薄
	 * @param file
	 *            文件对象
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void write(Workbook workbook, File file) throws IOException {
		if (workbook == null) {
			throw new IllegalArgumentException("Illegal workbook:" + workbook);
		}
		if (file == null) {
			throw new IllegalArgumentException("Illegal file:" + file);
		}
		OutputStream output = new FileOutputStream(file);
		try {
			workbook.write(output);
		} finally {
			try {
				workbook.close();
			} finally {
				output.close();
			}
		}
	}

	/**
	 * 将Excel数据写入文件
	 * 
	 * @param workbook
	 *            Excel文件工作薄
	 * @param file
	 *            文件对象
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void write(Workbook workbook, Nfile file) throws IOException {
		if (workbook == null) {
			throw new IllegalArgumentException("Illegal workbook:" + workbook);
		}
		if (file == null) {
			throw new IllegalArgumentException("Illegal file:" + file);
		}
		OutputStream output = file.getOutputStream();
		try {
			workbook.write(output);
		} finally {
			try {
				workbook.close();
			} finally {
				output.close();
			}
		}
	}

	/**
	 * 拷贝单元格对象数据
	 * 
	 * @param source
	 *            原始单元格对象
	 * @param target
	 *            目标单元格对象
	 */
	public static void copy(Cell source, Cell target) {
		if (source == null) {
			throw new IllegalArgumentException("Illegal source:" + source);
		}
		if (target == null) {
			throw new IllegalArgumentException("Illegal target:" + target);
		}
		int type = source.getCellType();
		if (type == Cell.CELL_TYPE_BOOLEAN) {
			target.setCellValue(source.getBooleanCellValue());
		} else if (type == Cell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(source)) {
				target.setCellValue(source.getDateCellValue());
			} else {
				target.setCellValue(source.getNumericCellValue());
			}
		} else {
			target.setCellValue(source.getStringCellValue());
		}
	}

	/**
	 * 拷贝行对象数据
	 * 
	 * @param source
	 *            原始行对象
	 * @param target
	 *            目标行对象
	 */
	public static void copy(Row source, Row target) {
		if (source == null) {
			throw new IllegalArgumentException("Illegal source:" + source);
		}
		if (target == null) {
			throw new IllegalArgumentException("Illegal target:" + target);
		}
		for (int i = 0; i < source.getLastCellNum(); i++) {
			copy(source.getCell(i), target.createCell(i));
		}
	}

	/**
	 * 判断Excel数据行是否为空
	 * 
	 * @param row
	 *            Excel数据行对象
	 * @return true/false
	 */
	public static boolean isEmpty(Row row) {
		if (row == null) {
			return true;
		}
		for (int i = 0; i < row.getLastCellNum(); i++) {
			if (getValue(row.getCell(i)) != null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取Excel单元格值
	 * 
	 * @param cell
	 *            Excel单元格对象
	 * @return 值
	 */
	public static Object getValue(Cell cell) {
		if (cell == null) {
			return null;
		}
		int type = cell.getCellType();
		if (type == Cell.CELL_TYPE_BOOLEAN) {
			return cell.getBooleanCellValue();
		} else if (type == Cell.CELL_TYPE_NUMERIC) {
			return HSSFDateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue() : cell.getNumericCellValue();
		}
		String value = Strings.clean(cell.getStringCellValue()).trim();
		return value.isEmpty() ? null : value;
	}

	/**
	 * 获取Excel单元格值
	 * 
	 * @param <T>
	 *            数据类型
	 * @param cell
	 *            Excel单元格对象
	 * @param type
	 *            值类型
	 * @return 值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getValue(Cell cell, Class<T> type) {
		if (cell == null) {
			return null;
		} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC && Date.class.isAssignableFrom(type)) {
			return (T) cell.getDateCellValue();
		}
		Object value = getValue(cell);
		if (value == null) {
			return null;
		} else if (type == String.class) {
			if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				String string = new BigDecimal((Double) value).toString();
				return string.endsWith(".0") ? (T) string.substring(0, string.length() - 2) : (T) string;
			}
			return (T) value.toString();
		}
		return (T) Beans.toObject(type, value);
	}

	/**
	 * 获取Excel一行单元格的值，如果所有值都为空则返回空数组
	 * 
	 * @param row
	 *            Excel行对象
	 * @return 值数组
	 */
	public static Object[] getValues(Row row) {
		return getValues(row, Object.class);
	}

	/**
	 * 获取Excel一行单元格的值，如果所有值都为空则返回空数组
	 * 
	 * @param <T>
	 *            数据类型
	 * @param row
	 *            Excel行对象
	 * @param type
	 *            数据类型
	 * @return 值数组
	 */
	public static <T> T[] getValues(Row row, Class<T> type) {
		if (row == null) {
			throw new IllegalArgumentException("Illegal row:" + row);
		}
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		boolean empty = true;
		int columns = row.getLastCellNum(); // 从1开始
		T[] values = Beans.getArray(type, columns);
		for (int i = 0; i < columns; i++) {
			if ((values[i] = getValue(row.getCell(i), type)) != null && empty) {
				empty = false;
			}
		}
		if (empty) {
			return Beans.getArray(type, 0);
		}
		for (int i = columns - 1; i >= 0; i--) {
			if (values[i] == null) {
				columns--;
				continue;
			}
			break;
		}
		return columns == values.length ? values : Arrays.copyOf(values, columns);
	}

	/**
	 * 获取单元格日期值
	 * 
	 * @param cell
	 *            单元格对象
	 * @param formats
	 *            日期格式数组
	 * @return 日期对象
	 */
	public static Date getDate(Cell cell, String... formats) {
		if (cell == null) {
			return null;
		} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			return cell.getDateCellValue();
		}
		String value = getString(cell);
		return value == null ? null : Dates.parse(value, formats);
	}

	/**
	 * 获取单元格文本值
	 * 
	 * @param cell
	 *            单元格对象
	 * @return 数据文本
	 */
	public static String getString(Cell cell) {
		Object value = cell == null ? null : getValue(cell);
		if (value != null && cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			String string = new BigDecimal((Double) value).toString();
			return string.endsWith(".0") ? string.substring(0, string.length() - 2) : string;
		}
		return Strings.toString(value);
	}

	/**
	 * 获取单元格数值
	 * 
	 * @param cell
	 *            单元格对象
	 * @return 数值
	 */
	public static Double getNumber(Cell cell) {
		if (cell == null) {
			return null;
		} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			return cell.getNumericCellValue();
		}
		String value = getString(cell);
		return value == null ? null : Double.parseDouble(value);
	}

	/**
	 * 获取单元格Boolean值
	 * 
	 * @param cell
	 *            单元格对象
	 * @return true/false
	 */
	public static Boolean getBoolean(Cell cell) {
		if (cell == null) {
			return null;
		} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			return cell.getBooleanCellValue();
		}
		String value = getString(cell);
		return value == null ? null : Boolean.parseBoolean(value);
	}

	/**
	 * 设置Excel单元格值
	 * 
	 * @param cell
	 *            Excel单元格对象
	 * @param value
	 *            值
	 */
	public static void setValue(Cell cell, Object value) {
		if (cell == null) {
			throw new IllegalArgumentException("Illegal cell:" + cell);
		}
		if (!Beans.isEmpty(value)) {
			if (value instanceof Object[]) {
				value = Strings.join((Object[]) value, ',');
			} else if (value instanceof Collection) {
				value = Strings.join((Collection<?>) value, ',');
			}
			cell.setCellValue(Strings.toString(value));
		}
	}

	/**
	 * 设置Excel单元格值
	 * 
	 * @param row
	 *            Excel行对象
	 * @param values
	 *            单元格值数组
	 */
	public static void setValues(Row row, Object... values) {
		if (row == null) {
			throw new IllegalArgumentException("Illegal row:" + row);
		}
		for (int i = 0; i < values.length; i++) {
			setValue(row.createCell(i), values[i]);
		}
	}

	/**
	 * 设置Excel文件标题
	 * 
	 * @param row
	 *            Excel数据行对象
	 * @param titles
	 *            标题数组
	 */
	public static void setTitles(Row row, String... titles) {
		if (row == null) {
			throw new IllegalArgumentException("Illegal row:" + row);
		}
		if (titles != null && titles.length > 0) {
			Workbook workbook = row.getSheet().getWorkbook();
			Font font = workbook.createFont();
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			CellStyle style = workbook.createCellStyle();
			style.setFont(font);
			style.setAlignment(CellStyle.ALIGN_CENTER);
			for (int c = 0; c < titles.length; c++) {
				Cell cell = row.createCell(c);
				cell.setCellStyle(style);
				cell.setCellValue(titles[c]);
			}
		}
	}

	/**
	 * 获取Excel文件总行数
	 * 
	 * @param file
	 *            Excel文件
	 * @return 总行数
	 * @throws IOException
	 *             IO操作异常
	 */
	public static int getCount(Nfile file) throws IOException {
		return getCount(file, 0);
	}

	/**
	 * 获取Excel文件总行数
	 * 
	 * @param file
	 *            Excel文件
	 * @param start
	 *            开始数据行下标（从0开始）
	 * @return 总行数
	 * @throws IOException
	 *             IO操作异常
	 */
	public static int getCount(Nfile file, int start) throws IOException {
		Workbook workbook = getWorkbook(file);
		try {
			return getCount(workbook, start);
		} finally {
			workbook.close();
		}
	}

	/**
	 * 获取Excel数据总行数
	 * 
	 * @param sheet
	 *            Excel sheet
	 * @return 总行数
	 * @throws IOException
	 *             IO操作异常
	 */
	public static int getCount(Sheet sheet) throws IOException {
		return getCount(sheet, 0);
	}

	/**
	 * 获取Excel数据总行数
	 * 
	 * @param workbook
	 *            Excel文件工作薄
	 * @return 总行数
	 * @throws IOException
	 *             IO操作异常
	 */
	public static int getCount(Workbook workbook) throws IOException {
		return getCount(workbook, 0);
	}

	/**
	 * 获取Excel数据总行数
	 * 
	 * @param sheet
	 *            Excel sheet
	 * @param start
	 *            开始数据行下标（从0开始）
	 * @return 总行数
	 * @throws IOException
	 *             IO操作异常
	 */
	public static int getCount(Sheet sheet, int start) throws IOException {
		if (sheet == null) {
			throw new IllegalArgumentException("Illegal sheet:" + sheet);
		}
		if (start < 0) {
			throw new IllegalArgumentException("Illegal start:" + start);
		}
		return sheet.getPhysicalNumberOfRows() - start;
	}

	/**
	 * 获取Excel文件总行数
	 * 
	 * @param workbook
	 *            Excel文件工作薄
	 * @param start
	 *            开始数据行下标（从0开始）
	 * @return 总行数
	 * @throws IOException
	 *             IO操作异常
	 */
	public static int getCount(Workbook workbook, int start) throws IOException {
		if (workbook == null) {
			throw new IllegalArgumentException("Illegal workbook:" + workbook);
		}
		if (start < 0) {
			throw new IllegalArgumentException("Illegal start:" + start);
		}
		int count = 0;
		for (int i = 0, sheets = workbook.getNumberOfSheets(); i < sheets; i++) {
			count += getCount(workbook.getSheetAt(i), start);
		}
		return count;
	}

	/**
	 * 获取Excel文件标题
	 * 
	 * @param file
	 *            Excel文件
	 * @return 标题数组
	 * @throws IOException
	 *             IO操作异常
	 */
	public static String[] getTitles(Nfile file) throws IOException {
		return getTitles(file, 0);
	}

	/**
	 * 获取Excel文件标题
	 * 
	 * @param file
	 *            Excel文件
	 * @param index
	 *            标题数据行下标（从0开始）
	 * @return 标题数组
	 * @throws IOException
	 *             IO操作异常
	 */
	public static String[] getTitles(Nfile file, int index) throws IOException {
		Workbook workbook = getWorkbook(file);
		try {
			return getTitles(workbook, index);
		} finally {
			workbook.close();
		}
	}

	/**
	 * 获取Excel文件标题
	 * 
	 * @param sheet
	 *            Excel sheet
	 * @return 标题数组
	 * @throws IOException
	 *             IO操作异常
	 */
	public static String[] getTitles(Sheet sheet) throws IOException {
		return getTitles(sheet, 0);
	}

	/**
	 * 获取Excel文件标题
	 * 
	 * @param workbook
	 *            Excel文件工作薄
	 * @return 标题数组
	 * @throws IOException
	 *             IO操作异常
	 */
	public static String[] getTitles(Workbook workbook) throws IOException {
		return getTitles(workbook, 0);
	}

	/**
	 * 获取Excel文件标题
	 * 
	 * @param sheet
	 *            Excel sheet
	 * @param index
	 *            标题数据行下标（从0开始）
	 * @return 标题数组
	 * @throws IOException
	 *             IO操作异常
	 */
	public static String[] getTitles(Sheet sheet, int index) throws IOException {
		if (sheet == null) {
			throw new IllegalArgumentException("Illegal sheet:" + sheet);
		}
		if (index < 0) {
			throw new IllegalArgumentException("Illegal index:" + index);
		}
		return getValues(sheet.getRow(index), String.class);
	}

	/**
	 * 获取Excel文件标题
	 * 
	 * @param workbook
	 *            Excel文件工作薄
	 * @param index
	 *            标题数据行下标（从0开始）
	 * @return 标题数组
	 * @throws IOException
	 *             IO操作异常
	 */
	public static String[] getTitles(Workbook workbook, int index) throws IOException {
		if (workbook == null) {
			throw new IllegalArgumentException("Illegal workbook:" + workbook);
		}
		if (index < 0) {
			throw new IllegalArgumentException("Illegal index:" + index);
		}
		return workbook.getNumberOfSheets() == 0 ? Strings.EMPTY_ARRAY : getTitles(workbook.getSheetAt(0), index);
	}

	/**
	 * 将Excel行对象数据转换成对象实例
	 * 
	 * @param <M>
	 *            数据类型
	 * @param row
	 *            Excel行对象
	 * @param type
	 *            目标对象类型
	 * @param fields
	 *            目标对象字段数组
	 * @return 目标对象实例
	 */
	public static <M> M getObject(Row row, Class<M> type, Field... fields) {
		if (row == null) {
			throw new IllegalArgumentException("Illegal row:" + row);
		}
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		int initialized = 0; // 设置属性个数
		M entity = Beans.getInstance(type);
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			Object value = getValue(row.getCell(i), field.getType());
			if (value == null) {
				continue;
			}
			Beans.setValue(entity, field, value);
			initialized++;
		}
		return initialized == 0 ? null : entity;
	}

	/**
	 * 将Excel行对象数据转换成对象实例
	 * 
	 * @param <M>
	 *            数据类型
	 * @param row
	 *            Excel行对象
	 * @param type
	 *            目标对象类型
	 * @param properties
	 *            目标对象属性名称数组
	 * @return 目标对象实例
	 */
	public static <M> M getObject(Row row, Class<M> type, String... properties) {
		if (row == null) {
			throw new IllegalArgumentException("Illegal row:" + row);
		}
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		return getObject(row, type, Beans.getFields(type, properties));
	}

	/**
	 * 从Excel文件中获取对象实体
	 * 
	 * @param <M>
	 *            数据类型
	 * @param file
	 *            Excel文件
	 * @param type
	 *            对象类型
	 * @param properties
	 *            目标对象属性名称数组
	 * @return 对象实体列表
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> List<M> getObjects(Nfile file, Class<M> type, String... properties) throws IOException {
		return getObjects(file, 0, type, properties);
	}

	/**
	 * 从Excel文件中获取对象实体
	 * 
	 * @param <M>
	 *            数据类型
	 * @param sheet
	 *            Excel sheet
	 * @param type
	 *            对象类型
	 * @param properties
	 *            目标对象属性名称数组
	 * @return 对象实体列表
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> List<M> getObjects(Sheet sheet, Class<M> type, String... properties) throws IOException {
		return getObjects(sheet, 0, type, properties);
	}

	/**
	 * 从Excel文件中获取对象实体
	 * 
	 * @param <M>
	 *            数据类型
	 * @param workbook
	 *            Excel文件工作薄
	 * @param type
	 *            对象类型
	 * @param properties
	 *            目标对象属性名称数组
	 * @return 对象实体列表
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> List<M> getObjects(Workbook workbook, Class<M> type, String... properties) throws IOException {
		return getObjects(workbook, 0, type, properties);
	}

	/**
	 * 从Excel文件中获取对象实体
	 * 
	 * @param <M>
	 *            数据类型
	 * @param file
	 *            Excel文件
	 * @param start
	 *            开始数据行下标（从0开始）
	 * @param type
	 *            对象类型
	 * @param properties
	 *            目标对象属性名称数组
	 * @return 对象实体列表
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> List<M> getObjects(Nfile file, int start, Class<M> type, String... properties)
			throws IOException {
		Workbook workbook = getWorkbook(file);
		try {
			return getObjects(workbook, start, type, properties);
		} finally {
			workbook.close();
		}
	}

	/**
	 * 从Excel文件中获取对象实体
	 * 
	 * @param <M>
	 *            数据类型
	 * @param sheet
	 *            Excel sheet
	 * @param start
	 *            开始数据行下标（从0开始）
	 * @param type
	 *            对象类型
	 * @param properties
	 *            目标对象属性名称数组
	 * @return 对象实体列表
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> List<M> getObjects(Sheet sheet, int start, final Class<M> type, String... properties)
			throws IOException {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		final Field[] fields = Beans.getFields(type, properties);
		return getObjects(sheet, start, new Reader<M>() {

			@Override
			public M read(Row row, int count) {
				return getObject(row, type, fields);
			}

		});
	}

	/**
	 * 从Excel文件中获取对象实体
	 * 
	 * @param <M>
	 *            数据类型
	 * @param workbook
	 *            Excel文件工作薄
	 * @param start
	 *            开始数据行下标（从0开始）
	 * @param type
	 *            对象类型
	 * @param properties
	 *            目标对象属性名称数组
	 * @return 对象实体列表
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> List<M> getObjects(Workbook workbook, int start, final Class<M> type, String... properties)
			throws IOException {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		final Field[] fields = Beans.getFields(type, properties);
		return getObjects(workbook, start, new Reader<M>() {

			@Override
			public M read(Row row, int count) {
				return getObject(row, type, fields);
			}

		});
	}

	/**
	 * 从Excel文件中获取对象实体
	 * 
	 * @param <M>
	 *            数据类型
	 * @param file
	 *            Excel文件
	 * @param reader
	 *            Excel对象实体读取接口
	 * @return 对象实体列表
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> List<M> getObjects(Nfile file, Reader<M> reader) throws IOException {
		return getObjects(file, 0, reader);
	}

	/**
	 * 从Excel文件中获取对象实体
	 * 
	 * @param <M>
	 *            数据类型
	 * @param sheet
	 *            Excel sheet
	 * @param reader
	 *            Excel对象实体读取接口
	 * @return 对象实体列表
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> List<M> getObjects(Sheet sheet, Reader<M> reader) throws IOException {
		return getObjects(sheet, 0, reader);
	}

	/**
	 * 从Excel文件中获取对象实体
	 * 
	 * @param <M>
	 *            数据类型
	 * @param workbook
	 *            Excel文件工作薄
	 * @param reader
	 *            Excel对象实体读取接口
	 * @return 对象实体列表
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> List<M> getObjects(Workbook workbook, Reader<M> reader) throws IOException {
		return getObjects(workbook, 0, reader);
	}

	/**
	 * 从Excel文件中获取对象实体
	 * 
	 * @param <M>
	 *            数据类型
	 * @param file
	 *            Excel文件
	 * @param start
	 *            开始数据行下标（从0开始）
	 * @param reader
	 *            Excel对象实体读取接口
	 * @return 对象实体列表
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> List<M> getObjects(Nfile file, int start, Reader<M> reader) throws IOException {
		Workbook workbook = getWorkbook(file);
		try {
			return getObjects(workbook, start, reader);
		} finally {
			workbook.close();
		}
	}

	/**
	 * 从Excel文件中获取对象实体
	 * 
	 * @param <M>
	 *            数据类型
	 * @param sheet
	 *            Excel sheet
	 * @param start
	 *            开始数据行下标（从0开始）
	 * @param reader
	 *            Excel对象实体读取接口
	 * @return 对象实体列表
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> List<M> getObjects(Sheet sheet, int start, Reader<M> reader) throws IOException {
		if (sheet == null) {
			throw new IllegalArgumentException("Illegal sheet:" + sheet);
		}
		if (start < 0) {
			throw new IllegalArgumentException("Illegal start:" + start);
		}
		if (reader == null) {
			throw new IllegalArgumentException("Illegal reader:" + reader);
		}
		int count = 0;
		List<M> objects = new LinkedList<M>();
		for (int r = start, rows = sheet.getLastRowNum(); r <= rows; r++) {
			Row row = sheet.getRow(r);
			if (!isEmpty(row)) {
				M object = reader.read(row, ++count);
				if (object != null) {
					objects.add(object);
				}
			}
		}
		return objects;
	}

	/**
	 * 从Excel文件中获取对象实体
	 * 
	 * @param <M>
	 *            数据类型
	 * @param workbook
	 *            Excel文件工作薄
	 * @param start
	 *            开始数据行下标（从0开始）
	 * @param reader
	 *            Excel对象实体读取接口
	 * @return 对象实体列表
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> List<M> getObjects(Workbook workbook, int start, Reader<M> reader) throws IOException {
		if (workbook == null) {
			throw new IllegalArgumentException("Illegal workbook:" + workbook);
		}
		if (start < 0) {
			throw new IllegalArgumentException("Illegal start:" + start);
		}
		if (reader == null) {
			throw new IllegalArgumentException("Illegal reader:" + reader);
		}
		int count = 0;
		List<M> objects = new LinkedList<M>();
		for (int i = 0, sheets = workbook.getNumberOfSheets(); i < sheets; i++) {
			Sheet sheet = workbook.getSheetAt(i);
			for (int r = start, rows = sheet.getLastRowNum(); r <= rows; r++) {
				Row row = sheet.getRow(r);
				if (!isEmpty(row)) {
					M object = reader.read(row, ++count);
					if (object != null) {
						objects.add(object);
					}
				}
			}
		}
		return objects;
	}

	/**
	 * 将对象实例转换成Excel行对象
	 * 
	 * @param row
	 *            目标Excel行对象
	 * @param object
	 *            源对象实例
	 * @param fields
	 *            需要转换的字段数组
	 */
	public static void setObject(Row row, Object object, Field... fields) {
		if (row == null) {
			throw new IllegalArgumentException("Illegal row:" + row);
		}
		if (object == null) {
			throw new IllegalArgumentException("Illegal object:" + object);
		}
		for (int i = 0; i < fields.length; i++) {
			Object value = Beans.getValue(object, fields[i]);
			if (value != null) {
				setValue(row.createCell(i), value);
			}
		}
	}

	/**
	 * 将对象实例转换成Excel行对象
	 * 
	 * @param row
	 *            目标Excel行对象
	 * @param object
	 *            源对象实例
	 * @param properties
	 *            需要转换的属性名称数组
	 */
	public static void setObject(Row row, Object object, String... properties) {
		if (row == null) {
			throw new IllegalArgumentException("Illegal row:" + row);
		}
		if (object == null) {
			throw new IllegalArgumentException("Illegal object:" + object);
		}
		setObject(row, object, Beans.getFields(object.getClass(), properties));
	}

	/**
	 * 将对象实体设置到Excel文件中
	 * 
	 * @param file
	 *            Excel文件
	 * @param objects
	 *            对象实体列表
	 * @param properties
	 *            需要转换的属性名称数组
	 * @return 设置数量
	 * @throws IOException
	 *             IO操作异常
	 */
	public static int setObjects(Nfile file, List<?> objects, String... properties) throws IOException {
		return setObjects(file, 0, objects, properties);
	}

	/**
	 * 将对象实体设置到Excel文件中
	 * 
	 * @param sheet
	 *            Excel sheet
	 * @param objects
	 *            对象实体列表
	 * @param properties
	 *            需要转换的属性名称数组
	 * @return 设置数量
	 * @throws IOException
	 *             IO操作异常
	 */
	public static int setObjects(Sheet sheet, List<?> objects, String... properties) throws IOException {
		return setObjects(sheet, 0, objects, properties);
	}

	/**
	 * 将对象实体设置到Excel文件中
	 * 
	 * @param workbook
	 *            Excel文件工作薄
	 * @param objects
	 *            对象实体列表
	 * @param properties
	 *            需要转换的属性名称数组
	 * @return 设置数量
	 * @throws IOException
	 *             IO操作异常
	 */
	public static int setObjects(Workbook workbook, List<?> objects, String... properties) throws IOException {
		return setObjects(workbook, 0, objects, properties);
	}

	/**
	 * 将对象实体设置到Excel文件中
	 * 
	 * @param <M>
	 *            数据类型
	 * @param file
	 *            Excel文件
	 * @param start
	 *            开始数据行下标（从0开始）
	 * @param objects
	 *            对象实体列表
	 * @param properties
	 *            需要转换的属性名称数组
	 * @return 设置数量
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> int setObjects(Nfile file, int start, List<M> objects, String... properties) throws IOException {
		Workbook workbook = getWorkbook();
		int count = setObjects(workbook, start, objects, properties);
		write(workbook, file);
		return count;
	}

	/**
	 * 将对象实体设置到Excel文件中
	 * 
	 * @param <M>
	 *            数据类型
	 * @param sheet
	 *            Excel sheet
	 * @param start
	 *            开始数据行下标（从0开始）
	 * @param objects
	 *            对象实体列表
	 * @param properties
	 *            需要转换的属性名称数组
	 * @return 设置数量
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> int setObjects(Sheet sheet, int start, List<M> objects, String... properties) throws IOException {
		if (objects == null) {
			throw new IllegalArgumentException("Illegal objects:" + objects);
		}
		if (objects.isEmpty()) {
			return 0;
		}
		final Field[] fields = Beans.getFields(objects.get(0).getClass(), properties);
		return setObjects(sheet, start, objects, new Writer<M>() {

			@Override
			public void write(M entity, Row row, int count) {
				setObject(row, entity, fields);
			}

		});
	}

	/**
	 * 将对象实体设置到Excel文件中
	 * 
	 * @param <M>
	 *            数据类型
	 * @param workbook
	 *            Excel文件工作薄
	 * @param start
	 *            开始数据行下标（从0开始）
	 * @param objects
	 *            对象实体列表
	 * @param properties
	 *            需要转换的属性名称数组
	 * @return 设置数量
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> int setObjects(Workbook workbook, int start, List<M> objects, String... properties)
			throws IOException {
		if (objects == null) {
			throw new IllegalArgumentException("Illegal objects:" + objects);
		}
		if (objects.isEmpty()) {
			return 0;
		}
		final Field[] fields = Beans.getFields(objects.get(0).getClass(), properties);
		return setObjects(workbook, start, objects, new Writer<M>() {

			@Override
			public void write(M entity, Row row, int count) {
				setObject(row, entity, fields);
			}

		});
	}

	/**
	 * 将对象实体设置到Excel文件中
	 * 
	 * @param <M>
	 *            数据类型
	 * @param file
	 *            Excel文件
	 * @param objects
	 *            对象实体列表
	 * @param writer
	 *            Excel对象实体写入接口
	 * @return 设置数量
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> int setObjects(Nfile file, List<M> objects, Writer<M> writer) throws IOException {
		return setObjects(file, 0, objects, writer);
	}

	/**
	 * 将对象实体设置到Excel文件中
	 * 
	 * @param <M>
	 *            数据类型
	 * @param sheet
	 *            Excel sheet
	 * @param objects
	 *            对象实体列表
	 * @param writer
	 *            Excel对象实体写入接口
	 * @return 设置数量
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> int setObjects(Sheet sheet, List<M> objects, Writer<M> writer) throws IOException {
		return setObjects(sheet, 0, objects, writer);
	}

	/**
	 * 将对象实体设置到Excel文件中
	 * 
	 * @param <M>
	 *            数据类型
	 * @param workbook
	 *            Excel文件工作薄
	 * @param objects
	 *            对象实体列表
	 * @param writer
	 *            Excel对象实体写入接口
	 * @return 设置数量
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> int setObjects(Workbook workbook, List<M> objects, Writer<M> writer) throws IOException {
		return setObjects(workbook, 0, objects, writer);
	}

	/**
	 * 将对象实体设置到Excel文件中
	 * 
	 * @param <M>
	 *            数据类型
	 * @param file
	 *            Excel文件
	 * @param start
	 *            开始数据行下标（从0开始）
	 * @param objects
	 *            对象实体列表
	 * @param writer
	 *            Excel对象实体写入接口
	 * @return 设置数量
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> int setObjects(Nfile file, int start, List<M> objects, Writer<M> writer) throws IOException {
		Workbook workbook = getWorkbook();
		int count = setObjects(workbook, start, objects, writer);
		write(workbook, file);
		return count;
	}

	/**
	 * 将对象实体设置到Excel文件中
	 * 
	 * @param <M>
	 *            数据类型
	 * @param sheet
	 *            Excel sheet
	 * @param start
	 *            开始数据行下标（从0开始）
	 * @param objects
	 *            对象实体列表
	 * @param writer
	 *            Excel对象实体写入接口
	 * @return 设置数量
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> int setObjects(Sheet sheet, int start, List<M> objects, Writer<M> writer) throws IOException {
		if (sheet == null) {
			throw new IllegalArgumentException("Illegal sheet:" + sheet);
		}
		if (start < 0) {
			throw new IllegalArgumentException("Illegal start:" + start);
		}
		if (objects == null) {
			throw new IllegalArgumentException("Illegal objects:" + objects);
		}
		if (writer == null) {
			throw new IllegalArgumentException("Illegal writer:" + writer);
		}
		int count = 0;
		for (int i = 0; i < objects.size(); i++) {
			M object = objects.get(i);
			if (object != null) {
				writer.write(object, sheet.createRow(start++), ++count);
			}
		}
		return count;
	}

	/**
	 * 将对象实体设置到Excel文件中
	 * 
	 * @param <M>
	 *            数据类型
	 * @param workbook
	 *            Excel文件工作薄
	 * @param start
	 *            开始数据行下标（从0开始）
	 * @param objects
	 *            对象实体列表
	 * @param writer
	 *            Excel对象实体写入接口
	 * @return 设置数量
	 * @throws IOException
	 *             IO操作异常
	 */
	public static <M> int setObjects(Workbook workbook, int start, List<M> objects, Writer<M> writer)
			throws IOException {
		if (workbook == null) {
			throw new IllegalArgumentException("Illegal workbook:" + workbook);
		}
		if (start < 0) {
			throw new IllegalArgumentException("Illegal start:" + start);
		}
		if (objects == null) {
			throw new IllegalArgumentException("Illegal objects:" + objects);
		}
		if (writer == null) {
			throw new IllegalArgumentException("Illegal writer:" + writer);
		}
		int count = 0;
		int r = start;
		Sheet sheet = null;
		for (int i = 0; i < objects.size(); i++) {
			if (i % 50000 == 0) {
				r = start;
				sheet = workbook.createSheet();
			}
			M object = objects.get(i);
			if (object != null) {
				writer.write(object, sheet.createRow(r++), ++count);
			}
		}
		return count;
	}

	/**
	 * Excel文件迭代
	 * 
	 * @param file
	 *            Excel文件
	 * @param reader
	 *            Excel对象实体读取接口
	 * @return 读取数量
	 * @throws IOException
	 *             IO操作异常
	 */
	public static int iteration(Nfile file, Reader<?> reader) throws IOException {
		return iteration(file, 0, reader);
	}

	/**
	 * Excel文件迭代
	 * 
	 * @param sheet
	 *            Excel sheet
	 * @param reader
	 *            Excel对象实体读取接口
	 * @return 读取数量
	 * @throws IOException
	 *             IO操作异常
	 */
	public static int iteration(Sheet sheet, Reader<?> reader) throws IOException {
		return iteration(sheet, 0, reader);
	}

	/**
	 * Excel文件迭代
	 * 
	 * @param workbook
	 *            Excel文件工作薄
	 * @param reader
	 *            Excel对象实体读取接口
	 * @return 读取数量
	 * @throws IOException
	 *             IO操作异常
	 */
	public static int iteration(Workbook workbook, Reader<?> reader) throws IOException {
		return iteration(workbook, 0, reader);
	}

	/**
	 * Excel文件迭代
	 * 
	 * @param file
	 *            Excel文件
	 * @param start
	 *            开始数据行下标（从0开始）
	 * @param reader
	 *            Excel对象实体读取接口
	 * @return 读取数量
	 * @throws IOException
	 *             IO操作异常
	 */
	public static int iteration(Nfile file, int start, Reader<?> reader) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("Illegal file:" + file);
		}
		Workbook workbook = getWorkbook(file);
		try {
			return iteration(workbook, start, reader);
		} finally {
			workbook.close();
		}
	}

	/**
	 * Excel文件迭代
	 * 
	 * @param sheet
	 *            Excel sheet
	 * @param start
	 *            开始数据行下标（从0开始）
	 * @param reader
	 *            Excel对象实体读取接口
	 * @return 读取数量
	 * @throws IOException
	 *             IO操作异常
	 */
	public static int iteration(Sheet sheet, int start, Reader<?> reader) throws IOException {
		if (sheet == null) {
			throw new IllegalArgumentException("Illegal sheet:" + sheet);
		}
		if (start < 0) {
			throw new IllegalArgumentException("Illegal start:" + start);
		}
		if (reader == null) {
			throw new IllegalArgumentException("Illegal reader:" + reader);
		}
		int count = 0;
		for (int r = start, rows = sheet.getLastRowNum(); r <= rows; r++) {
			Row row = sheet.getRow(r);
			if (!isEmpty(row)) {
				reader.read(row, ++count);
			}
		}
		return count;
	}

	/**
	 * Excel文件迭代
	 * 
	 * @param workbook
	 *            Excel文件工作薄
	 * @param start
	 *            开始数据行下标（从0开始）
	 * @param reader
	 *            Excel对象实体读取接口
	 * @return 读取数量
	 * @throws IOException
	 *             IO操作异常
	 */
	public static int iteration(Workbook workbook, int start, Reader<?> reader) throws IOException {
		if (workbook == null) {
			throw new IllegalArgumentException("Illegal workbook:" + workbook);
		}
		if (start < 0) {
			throw new IllegalArgumentException("Illegal start:" + start);
		}
		if (reader == null) {
			throw new IllegalArgumentException("Illegal reader:" + reader);
		}
		int count = 0;
		for (int i = 0, sheets = workbook.getNumberOfSheets(); i < sheets; i++) {
			Sheet sheet = workbook.getSheetAt(i);
			for (int r = start, rows = sheet.getLastRowNum(); r <= rows; r++) {
				Row row = sheet.getRow(r);
				if (!isEmpty(row)) {
					reader.read(row, ++count);
				}
			}
		}
		return count;
	}

}
