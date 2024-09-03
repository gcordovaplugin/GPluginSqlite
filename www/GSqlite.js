var exec = require("cordova/exec");

const SERVICE_NAME = "GSqlite";
module.exports = {
  init: function (success) {
    exec(success, null, SERVICE_NAME, "init", [""]);
  },
  insert: function (sql, success) {
    exec(success, null, SERVICE_NAME, "insert", [sql]);
  },
  delete: function (sql, success) {
    exec(success, null, SERVICE_NAME, "delete", [sql]);
  },
  update: function (sql, success) {
    exec(success, null, SERVICE_NAME, "update", [sql]);
  },
  fetch: function (sql, success, args) {
    exec(success, null, SERVICE_NAME, "fetch", [sql, args]);
  },
  fetchAll: function (sql, success, args) {
    exec(success, null, SERVICE_NAME, "fetchAll", [sql, args]);
  },
};
