var exec = require('cordova/exec');

module.exports =  {
    echo(args,success,error){
        exec(success, error, 'Myplugin', 'echo', [args]);
    },
    newTask(args, success, error) {
        exec(success, error, 'Myplugin', 'newTask', [args]);
    },
    delTask(args, success, error) {
        exec(success, error, 'Myplugin', 'delTask', [args]);
    }
}
