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

import cn.hutool.core.util.CharsetUtil;
import ltd.huntinginfo.feng.common.xss.config.FengXssProperties;
import ltd.huntinginfo.feng.common.xss.utils.XssUtil;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.springframework.web.util.HtmlUtils;

/**
 * 默认的XSS清理器实现类，提供HTML内容的安全清理功能
 *
 * @author lengleng
 * @date 2025/05/31
 */
public class DefaultXssCleaner implements XssCleaner {

	private final FengXssProperties properties;

	public DefaultXssCleaner(FengXssProperties properties) {
		this.properties = properties;
	}

	/**
	 * 获取文档输出设置
	 * @param properties PigXss配置属性
	 * @return 文档输出设置对象
	 */
	private static Document.OutputSettings getOutputSettings(FengXssProperties properties) {
		return new Document.OutputSettings()
			// 2. 转义，没找到关闭的方法，目前这个规则最少
			.escapeMode(Entities.EscapeMode.xhtml)
			// 3. 保留换行
			.prettyPrint(properties.isPrettyPrint());
	}

	/**
	 * 清理HTML内容，根据XSS类型和模式进行处理
	 * @param bodyHtml 待清理的HTML内容
	 * @param type XSS处理类型
	 * @return 清理后的HTML内容
	 * @throws XssException 当模式为validate且内容不合法时抛出异常
	 */
	@Override
	public String clean(String bodyHtml, XssType type) {
		// 1. 为空直接返回
		if (StringUtil.isBlank(bodyHtml)) {
			return bodyHtml;
		}
		FengXssProperties.Mode mode = properties.getMode();
		if (FengXssProperties.Mode.escape == mode) {
			// html 转义
			return HtmlUtils.htmlEscape(bodyHtml, CharsetUtil.UTF_8);
		}
		else if (FengXssProperties.Mode.validate == mode) {
			// 校验
			if (Jsoup.isValid(bodyHtml, XssUtil.WHITE_LIST)) {
				return bodyHtml;
			}
			throw type.getXssException(bodyHtml, "Xss validate fail, input value:" + bodyHtml);
		}
		else {
			// 4. 清理后的 html
			String escapedHtml = Jsoup.clean(bodyHtml, "", XssUtil.WHITE_LIST, getOutputSettings(properties));
			if (properties.isEnableEscape()) {
				return escapedHtml;
			}
			// 5. 反转义
			return Entities.unescape(escapedHtml);
		}
	}

}
