var exec = require('cordova/exec');

module.exports =  {
    coolMethod(args, success, error) {
        exec(success, error, 'Myplugin', 'coolMethod', [args]);
    },
    echo(args,success,error){
        exec(success, error, 'Myplugin', 'echo', [args]);
    }
}
