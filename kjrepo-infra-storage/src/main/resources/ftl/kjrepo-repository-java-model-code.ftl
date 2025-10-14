package ${pkg};

<#list imports as import>
import ${import};
</#list>

public class ${name} {
	
	<#list fields as field>
	private ${field.type} ${field.name};
	</#list>
	
	<#list fields as field>
	public void set${field.name?cap_first}(${field.type} ${field.name}) {
		this.${field.name} = ${field.name};
	}
	public ${field.type} get${field.name?cap_first}() {
		return this.${field.name};
	}
	</#list>

}