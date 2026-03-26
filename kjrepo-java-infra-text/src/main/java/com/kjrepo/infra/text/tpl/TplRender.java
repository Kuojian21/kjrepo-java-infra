package com.kjrepo.infra.text.tpl;

import java.util.Map;

public interface TplRender {

	String render(String tpl, Map<String, Object> data);

}
