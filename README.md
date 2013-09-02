qmikjson
========

<p style="font-size:16px;">性能出众,高效的json解析引擎,是目前最高的解析引擎之一,比之fastjson有25%-200%左右的性能提升</p>

<p style="font-size:18px;">使用方式:</p>
<p>//全路径: org.qmik.qmikjson.JSON</p>
<p>//转换成对象,默认转换成map或list</p>
JSON.parse(jsontext);
JSON.parse(jsontext,Class);

<p style="font-size:18px;">//对象转换成json字符串</p>
json.toJSONString(bean);
json.toJSONString(map);
json.toJSONString(list);

<p style="font-size:18px;">//对象转换成json字符串,时间格式指定</p>
json.toJSONString(bean,dateformate);
json.toJSONString(map,dateformate);
json.toJSONString(list,dateformate);


<p style="font-size:18px;">一个对java bean对象转换成json字符串性能200%提升点的使用:</p>
例子:

User user=StrongBeanFactory.get(User.class);
user.setId(11);
user.setName("aaa");
json.toJSONString(bean);

<p style="font-size:18px;color:red;">这种的转换性能可以提升200%</p>