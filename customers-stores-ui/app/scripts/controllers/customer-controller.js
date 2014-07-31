'use strict';

/**
 * @ngdoc function
 * @name customersStoresUiApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the customersStoresUiApp
 */
angular.module('customersStoresUiApp')
  .controller('CustomerController', function ($scope, $log, $http, $state) {
    
    $scope.addCustomer = function () {
      $state.go('addCustomer');
    };
    $scope.viewCustomerDetails = function (customer) {
      $log.info('Show details for customer with Id: ' + customer.id);
      $state.go('customerDetails', {customerId: customer.id});
    };
    $scope.deleteCustomer = function (customer) {
      $log.info('Deleting customer with Id: ' + customer.id);
      $http.delete('/customers/' + customer.id).then(function() {
        $scope.loadCustomers();
      });
    };
    $scope.loadCustomers = function () {
      var customerPromise = $http.get('/customers');
      customerPromise.then(function(customers) {
        $log.info('Retrieved customers', customers);
        if (customers.data._embedded) {
          $scope.customers = customers.data._embedded.customers;
        }
      });
    };

    $scope.loadCustomers();
  })

  .controller('AddCustomerController', function ($scope, $state, $log, $http) {
    $scope.map = {
        center: {
            latitude: 45,
            longitude: -73
        },
        zoom: 12
    };
    $scope.customer = {
      firstName: '',
      lastName: '',
      address: {
        location: {
          latitude: 0,
          longitude: 0
        }
      }
    };
    $scope.goBack = function () {
      $state.go('customers');
    };
    $scope.getMyLocation = function () {
      $log.info('Retrieving Location Information');
      navigator.geolocation.getCurrentPosition(function success(data) {
        $log.info('Location data retrieved', data);
        var coordinates = data.coords;
        $scope.$apply(function() {
          $scope.customer.address.location.latitude = coordinates.latitude;
          $scope.customer.address.location.longitude = coordinates.longitude;
        });
      });
    };
    $scope.submitCustomer = function () {
      $log.info('Adding New Customer', $scope.customer);
      var addCustomerPromise = $http.post('/customers', $scope.customer);

      addCustomerPromise.then(function(response) {
        $log.info(response);
        $state.go('customers');
      });
    };
    $scope.$watch('customer.address.location', function() {
      if ($scope.customer.address.location.latitude && $scope.customer.address.location.longitude) {
          $scope.map.center.latitude=$scope.customer.address.location.latitude;
          $scope.map.center.longitude=$scope.customer.address.location.longitude;
      }
    }, true);
  })
  .controller('CustomerDetailsController', function ($scope, $state, $stateParams, $http, $log) {
    var customerId = $stateParams.customerId;
    var customerPromise = $http.get('/customers/' + customerId);

    customerPromise.then(function(customers) {
      $log.info(customers);
      $scope.customer = customers.data;

      $scope.map = {
        center: {
            latitude: $scope.customer.address.location.latitude,
            longitude: $scope.customer.address.location.longitude
        },
        zoom: 12
      };
      $log.info('Map Data', $scope.Map);
    });

    $scope.goBack = function () {
      $state.go('customers');
    };

    $scope.map = {
        center: {
            latitude: 45,
            longitude: -73
        },
        zoom: 12
    };

  });
