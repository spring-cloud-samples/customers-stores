'use strict';

/**
 * @ngdoc function
 * @name customersStoresUiApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the customersStoresUiApp
 */
angular.module('customersStoresUiApp')
  .controller('StoreController', function ($scope, $log, $http, $state, appConfiguration) {

    $scope.loadStores = function () {
      var storePromise = $http.get(appConfiguration.storeApiUrl + '/stores');
      storePromise.then(function(stores) {
        $log.info('Retrieved stores', stores);
        if (stores.data._embedded) {
          $scope.stores = stores.data._embedded.stores;
        }
      });
    };

    $scope.loadStores();
  });

