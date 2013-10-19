qmikjson
========

<h2>性能出众,高效的json解析引擎,是目前最高的解析引擎之一,比之fastjson有25%-200%左右的性能提升</h2>
<h2>它的更加适用于小数据对象的转换,在大数据对象上的转换,性能并不出色(建议数据在k级别,在M级别的数据量,就算是偏大,或大对象了)</h2>

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
json.toJSONStringWithDateFormat(bean,dateformate);
json.toJSONStringWithDateFormat(map,dateformate);
json.toJSONStringWithDateFormat(list,dateformate);

<h3>一个普通java bean对象转换成json字符串</h3>
例子:

User user=new User();
user.setId(11);
user.setName("aaa");
json.toJSONString(bean);


<h3>一个增强java bean对象转换成json字符串性能:</h3>
例子:

User user=JSON.newInstance(User.class);
user.setId(11);
user.setName("aaa");
json.toJSONString(bean);

<h2 style="font-size:18px;color:red;">这种的转换性能更优,极端情况下,会有多倍的性能提升</h2>