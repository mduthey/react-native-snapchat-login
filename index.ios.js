import { NativeModules, NativeEventEmitter } from 'react-native';
import { resolve } from 'uri-js';

export const RNSnapchatLogin = NativeModules.SnapchatLogin;
export const RNSnapchatLoginEmitter = new NativeEventEmitter(RNSnapchatLogin);

class SnapchatLogin {
  addListener(eventType, listener, context) {
    return RNSnapchatLoginEmitter.addListener(eventType, listener, context);
  }

  login() {
    return new Promise((resolve, reject) => {
      RNSnapchatLogin.login()
        .then((result) => {
          const resultJson = JSON.parse(result);
          if(resultJson.error) {
            reject(resultJson.error);
          } else { 
            this.getUserInfo()
              .then(resolve)
              .catch(reject);
          }
        })
        .catch(e => reject(e));
    });
  }

  async isLogged() {
    const result = await RNSnapchatLogin.isUserLoggedIn();
    const resultJSON = JSON.parse(result);
    return !!resultJSON.result;
  }

  async logout() {
    const result = await RNSnapchatLogin.logout();
    const resultJSON = JSON.parse(result);
    return !!resultJSON.result;
  }

  getUserInfo() {
    return new Promise((resolve, reject) => {
      RNSnapchatLogin.fetchUserData()
        .then((tmp) => {
          const data = JSON.parse(tmp);
          if (data === null) {
            resolve(null);
          } else {
            let counter = 0;
            const tmpListener = this.addListener('AccessToken', (res) => {
              // This method is called 2 times. 
              // The first one with null. The second one with the token
              if (counter == 0) {
                counter++;
              } else {
                data.accessToken = res.accessToken !== 'null' ? res.accessToken : null;
                tmpListener.remove();
                resolve(data);
              }
            });
            RNSnapchatLogin.getAccessToken();
          }
        })
        .catch(e => reject(e));
    });
  }
}

export default new SnapchatLogin();