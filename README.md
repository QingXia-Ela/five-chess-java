# Five Chess

## 前言

感觉也没啥好说的，可能唯一吐槽的就是今年要求悔棋和用 TCP，明明 UDP 更舒服

隔壁班哥们挺惨的，要写象棋，不过不需要联机罢了

本仓库实现的五子棋缺点在于UI太恶心了，如果有懂 JFrame 的 ✌ 请务必赏脸改一手

本仓库的通信协议扩展很容易，只要你在 Message 类下依葫芦画瓢进行添加与自定义即可

最恶心的还是写实验报告，真的写不了一点

## 通信协议

示例： `LOGIN_SUCCESS$114:514`

`$` 左侧为消息类型，右侧为消息体

### 共同消息

| MSG TYPE        | MSG EXPLAIN | BODY EXAMPLE |
|-----------------|-------------|--------------|
| OK              | 通用OK        |              |
| ERROR           | 通用错误消息      | Exception... |
| REGRET_RESPONSE | 是否同意悔棋      | True/False   |
| CHESS_REGRET    |             |              |
| CHESS_PLACE     | TYPE:X:Y    | BLACK:0:0    |
| CHAT            | 消息          | 114514       |
| CHAT_RECEIVE    | 消息接收        | 1919810      |
| HEARTBEAT       | 心跳包         |              |
| PLATE_FULL      | 棋盘已满        |              |

### Server

服务端发送消息类型：

| MSG TYPE        | MSG EXPLAIN | BODY EXAMPLE |
|-----------------|-------------|--------------|
| LOGIN_SUCCESS   | 棋盘宽:高:名字    | 114:514:MIKA |
| LOGIN_ERROR     | 登录错误原因      | 1337         |


### Client

客户端发送消息类型：

| MSG TYPE        | MSG EXPLAIN | BODY EXAMPLE |
|-----------------|-------------|--------------|
| LOGIN           | 用户名:密码      | foo:bar      |
| LOGOUT          | 用户名         | foo          |


## 模式

UDP