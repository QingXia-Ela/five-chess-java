# Five Chess

## 通信协议

以换行符结束

### Server

服务端发送消息类型：

| MSG TYPE       | MSG BODY |
|----------------|----------|
| LOGIN_SUCCESS  |          |
| LOGIN_ERROR    | 登录错误原因   |
| OK             |          |
| ERROR          | 错误消息     |
| LOGOUT_SUCCESS |          |
| LOGOUT_ERROR   | 登出错误原因   |

### Client

客户端发送消息类型：

| MSG TYPE     | MSG BODY |
|--------------|----------|
| LOGIN        | 用户名:密码   |
| LOGOUT       | 用户名      |
| CHAT         | 用户名:消息   |
| CHESS_PLACE  | X:Y      |
| CHESS_REGRET |          |


