import { NativeModules, NativeEventEmitter } from 'react-native';
import { resolve } from 'uri-js';

export const RNSnapchatLogin = NativeModules.SnapchatLogin;
export const RNSnapchatLoginEmitter = new NativeEventEmitter(RNSnapchatLogin);

export default class SnapchatLogin {
  static addListener(eventType, listener, context) {
    return RNSnapchatLoginEmitter.addListener(eventType, listener, context);
  }

  static login() {
    return new Promise((resolve, reject) => {
      const succeededListener = this.addListener('LoginSucceeded', (res) => {
        succeededListener.remove();
        failedListener.remove();
        this.getUserInfo().then(resolve).catch(reject); 
      });
      const failedListener = this.addListener('LoginFailed', (res) => {
        succeededListener.remove();
        failedListener.remove();
        resolve(false);
      });
      RNSnapchatLogin.login();
    });
  }

  static async isLogged() {
    const result = await RNSnapchatLogin.isUserLoggedIn();
    const resultJSON = JSON.parse(result);
    return !!resultJSON.result;
  }

  static async logout() {
    const result = await RNSnapchatLogin.logout();
    const resultJSON = JSON.parse(result);
    return !!resultJSON.result;
  }

  static getUserInfo() {
    return new Promise((resolve, reject) => {
      RNSnapchatLogin.fetchUserData()
        .then((tmp) => {
          const data = JSON.parse(tmp);
          if (data === null) {
            resolve(null);
          } else {
            resolve(data);
          }
        })
        .catch(e => reject(e));
    });
  }
}
