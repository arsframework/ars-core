// **********************************************************
// Author: wuyq
// Created date: 2016/09/08
// Description: 远程调用接口
// **********************************************************


#ifndef _ARS_ICE
#define _ARS_ICE

module ars {
	module invoke {
		module remote {
			module slice {
				sequence<byte> ByteArray; // 字节数组
				
				/**
				 * 请求令牌
				 *
				 **/
				struct Itoken {
					string code; // 令牌标识
					string attributes; // 令牌属性（JSON格式）
				};
				
				/**
				 * 请求结果
				 *
				 **/
				class Iresult {
				
				};
				
				/**
				 * 数据流结果类型
				 *
				 **/
				class Istream extends Iresult {
					string id; // 数据流标识
					string name; // 数据流名称
					long size; // 数据流大小（字节）
					bool file; // 是否是文件对象
				};
				
				/**
				 * 字符串结果类型
				 *
				 **/
				class Istring extends Iresult {
					string json; // json字符串
				};
				
				/**
				 * 远程资源
				 *
				 **/
				interface Resource {
					/**
					 * 远程调用
					 *
					 * 如果参数包含文件，则需要先将文件上传，然后将参数名称修改为：__file_ + 实际参数名称
					 *
					 * @param client 客户标识
					 * @param token 令牌对象
					 * @param uri 资源地址
					 * @param parameter 请求参数（JSON格式）
					 * @return 结果对象
					 *
					 **/
					["ami", "amd"] Iresult invoke(string client, Itoken token, string uri, string parameter);
					
					/**
					 * 文件上传
					 *
					 * @param name 文件名称
					 * @param buffer 文件字节数组缓冲区
					 * @param length 缓冲字节长度
					 *
					 **/
					["ami", "amd"] void upload(string name, ByteArray buffer, int length);
					
					/**
					 * 文件下载
					 *
					 * @param id 数据流标识
					 * @param index 文件流开始位置
					 * @param length 文件字节长度
					 * @return 文件数据字节数组
					 *
					 **/
					["ami", "amd"] ByteArray download(string id, int index, int length);
					
				};
			};
		};
	};
};
#endif