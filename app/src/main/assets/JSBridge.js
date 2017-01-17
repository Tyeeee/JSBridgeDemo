(function () {
    var userAgent = window.navigator.userAgent;
    var PROTOCOL_SCHEMA = "yjt";
    var increase = 1;
    var JSBridge = window.JSBridge || (window.JSBridge = {});

    var ExposeMethod = {
        callMethod: function (clazz, method, param, callback) {
            var port = PrivateMethod.generatePort();
            if (typeof callback !== 'function') {
                callback = null;
            }
            PrivateMethod.registerCallback(port, callback);
            PrivateMethod.callNativeMethod(clazz, port, method, param);
        },

        onComplete: function (port, result) {
            PrivateMethod.onNativeComplete(port, result);
        }

    };

    var PrivateMethod = {
        callbacks: {},
        registerCallback: function (port, callback) {
            if (callback) {
                PrivateMethod.callbacks[port] = callback;
            }
        },
        getCallback: function (port) {
            var call = {};
            if (PrivateMethod.callbacks[port]) {
                call.callback = PrivateMethod.callbacks[port];
            } else {
                call.callback = null;
            }
            return call;
        },
        unRegisterCallback: function (port) {
            if (PrivateMethod.callbacks[port]) {
                delete PrivateMethod.callbacks[port];
            }
        },
        onNativeComplete: function (port, result) {
            var resultJson = PrivateMethod.str2Json(result);
            var callback = PrivateMethod.getCallback(port).callback;
            PrivateMethod.unRegisterCallback(port);
            if (callback) {
                //执行回调
                callback && callback(resultJson);
            }
        },
        generatePort: function () {
            return Math.floor(Math.random() * (1 << 50)) + '' + increase++;
        },
        str2Json: function (str) {
            if (str && typeof str === 'string') {
                try {
                    return JSON.parse(str);
                } catch (e) {
                    return {
                        status: {
                            code: 1,
                            msg: 'params parse error!'
                        }
                    };
                }
            } else {
                return str || {};
            }
        },
        json2String: function (param) {
            if (param && typeof param === 'object') {
                return JSON.stringify(param);
            } else {
                return param || '';
            }
        },
        callNativeMethod: function (clazz, port, method, param) {
            if (PrivateMethod.isAndroid()) {
                var parameters = PrivateMethod.json2String(param);
                var uri = PROTOCOL_SCHEMA + "://" + clazz + ":" + port + "/" + method + "?" + parameters;
                window.prompt(uri, "");
            }
        },
        isAndroid: function () {
            var tmp = userAgent.toLowerCase();
            var android = tmp.indexOf("android") > -1;
            return !!android;
        },
        isIos: function () {
            var tmp = userAgent.toLowerCase();
            var ios = tmp.indexOf("iphone") > -1;
            return !!ios;
        }
    };
    for (var index in ExposeMethod) {
        if (ExposeMethod.hasOwnProperty(index)) {
            if (!Object.prototype.hasOwnProperty.call(JSBridge, index)) {
                JSBridge[index] = ExposeMethod[index];
            }
        }
    }
})();



