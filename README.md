# Five Chess

## 通信协议

以换行符结束

### 共同消息

| MSG TYPE        | MSG EXPLAIN | BODY EXAMPLE |
|-----------------|-------------|--------------|
| OK              | 通用OK        |              |
| ERROR           | 通用错误消息      | Exception... |
| REGRET_RESPONSE | 是否同意悔棋      | true/false   |
| CHESS_REGRET    |             |              |
| CHESS_PLACE     | TYPE:X:Y    | BLACK:0:0    |
| CHAT            | 消息          | 114514       |
| CHAT_RECEIVE    | 消息接收        | 1919810      |
| HEARTBEAT       | 心跳包         |              |

### Server

服务端发送消息类型：

| MSG TYPE        | MSG EXPLAIN | BODY EXAMPLE |
|-----------------|-------------|--------------|
| LOGIN_SUCCESS   | 棋盘宽高        | 114:514      |
| LOGIN_ERROR     | 登录错误原因      | 1337         |


### Client

客户端发送消息类型：

| MSG TYPE        | MSG EXPLAIN | BODY EXAMPLE |
|-----------------|-------------|--------------|
| LOGIN           | 用户名:密码      | foo:bar      |
| LOGOUT          | 用户名         | foo          |
