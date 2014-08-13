'use strict';

angular.module('customersStoresUiApp')
  .config(function ($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise('/customers');

    $stateProvider.state('customers', {
      url: '/customers',
      controller: 'CustomerController',
      templateUrl: 'views/customers.html'
    })
    .state('addCustomer', {
      url: '/customers/add',
      controller: 'AddCustomerController',
      templateUrl: 'views/add-customer.html'
    })
    .state('customerDetails', {
      url: '/customers/{customerId}',
      controller: 'CustomerDetailsController',
      templateUrl: 'views/customer-details.html'
    })
    .state('stores', {
      url: '/stores',
      controller: 'StoreController',
      templateUrl: 'views/stores.html'
    })
    .state('about', {
      url: '/about',
      templateUrl: 'views/about.html'
    });
  });