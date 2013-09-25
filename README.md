qmikjson
========

<h2>性能出众,高效的json解析引擎,是目前最高的解析引擎之一,比之fastjson有25%-200%左右的性能提升</h2>

<h3 style="font-size:18px;">使用方式:</h3>
<h4>//全路径: org.qmik.qmikjson.JSON</p>
<h4>//转换成对象,默认转换成map或list</p>
JSON.parse(jsontext);
JSON.parse(jsontext,Class);

<h3>//对象转换成json字符串</h3>
json.toJSONString(bean);
json.toJSONString(map);
json.toJSONString(list);

<h3>//对象转换成json字符串,时间格式指定</h3>
json.toJSONString(bean,dateformate);
json.toJSONString(map,dateformate);
json.toJSONString(list,dateformate);

<h3>一个普通java bean对象转换成json字符串</h3>
例子:

User user=new User();
user.setId(11);
user.setName("aaa");
json.toJSONString(bean);


<h3>一个增强java bean对象转换成json字符串性能:</h3>
例子:

User user=StrongBeanFactory.get(User.class);
user.setId(11);
user.setName("aaa");
json.toJSONString(bean);

<h2 style="font-size:18px;color:red;">这种的转换性能更优些</h2>