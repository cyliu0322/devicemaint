package com.maint.common.util;

public class FileUtil {
	
	/**
	 * 更改文件名
	 * @param oldFile 原文件名（包含后缀）
	 * @param newFileName 新文件名（不包含后缀）
	 * @return 返回新文件名（包含后缀）
	 */
	public static String updateFileName(String oldFile, String newFileName) {
		return newFileName+oldFile.substring(oldFile.indexOf("."));
	}

}
