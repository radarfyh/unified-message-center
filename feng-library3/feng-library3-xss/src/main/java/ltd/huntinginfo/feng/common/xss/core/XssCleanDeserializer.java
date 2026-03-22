/*
 * Copyright (c) 2019-2029, Dreamlu 卢春梦 (596392912@qq.com & www.dreamlu.net).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 *  Modified by radarfyh(Edison.Feng) on 2025-12-30.
 *  Copyright (c) 2026 radarfyh(Edison.Feng). All rights reserved.
 *
 *  This file is part of UnifiedMessageCenter and is distributed under the
 *  same license terms as the original work, with additional modifications
 *  as noted above.
 */

package ltd.huntinginfo.feng.common.xss.core;

import ltd.huntinginfo.feng.common.core.util.SpringContextHolder;
import ltd.huntinginfo.feng.common.xss.config.FengXssProperties;
import ltd.huntinginfo.feng.common.xss.utils.XssUtil;
import tools.jackson.core.JacksonException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Jackson XSS 处理反序列化器
 *
 * @author lengleng
 * @date 2025/05/31
 */
@Slf4j
public class XssCleanDeserializer extends XssCleanDeserializerBase {

	/**
	 * 清理文本中的XSS内容
	 * @param name 名称
	 * @param text 待清理的文本
	 * @return 清理后的文本
	 * @throws JacksonException IO异常
	 */
	@Override
	public String clean(String name, String text) throws JacksonException {
		// 读取 xss 配置
		FengXssProperties properties = SpringContextHolder.getBean(FengXssProperties.class);
		// 读取 XssCleaner bean
		XssCleaner xssCleaner = SpringContextHolder.getBean(XssCleaner.class);
		if (xssCleaner != null) {
			String value = xssCleaner.clean(XssUtil.trim(text, properties.isTrimText()));
			log.debug("Json property value:{} cleaned up by mica-xss, current value is:{}.", text, value);
			return value;
		}
		else {
			return XssUtil.trim(text, properties.isTrimText());
		}
	}

}
