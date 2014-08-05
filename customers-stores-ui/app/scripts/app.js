'use strict';

/**
 * @ngdoc overview
 * @name customersStoresUiApp
 * @description
 * # customersStoresUiApp
 *
 * Main module of the application.

 ,
    'google-maps'
 */
angular
  .module('customersStoresUiApp', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ui.router',
    'ngSanitize',
    'ngTouch',
    'google-maps'
  ])
  .constant('appConfiguration', {
    //e.g. http://myserver:9000/rest
    customerApiUrl: window.location.protocol + '//' + window.location.host,
    storeApiUrl: window.location.protocol + '//' + window.location.host
  })
  .config(function () {

  });
