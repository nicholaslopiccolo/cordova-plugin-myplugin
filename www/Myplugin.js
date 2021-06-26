var exec = require('cordova/exec');

module.exports =  {
    echo(args,success,error){
        exec(success, error, 'Myplugin', 'echo', [args]);
    },
    newTask(phone, message, nsec, success, error) {
        exec(success, error, 'Myplugin', 'newTask', [{phone:phone,message:message,nsec:nsec}]);
    },
    delTask(index, success, error) {
        exec(success, error, 'Myplugin', 'delTask', [{index:index}]);
    }
}
