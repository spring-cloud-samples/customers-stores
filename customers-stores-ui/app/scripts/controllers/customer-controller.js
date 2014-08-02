'use strict';

/**
 * @ngdoc function
 * @name customersStoresUiApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the customersStoresUiApp
 */
angular.module('customersStoresUiApp')
  .controller('CustomerController', function ($scope, $log, $http, $state, appConfiguration) {
    
    $scope.addCustomer = function () {
      $state.go('addCustomer');
    };
    $scope.viewCustomerDetails = function (customer) {
      $log.info('Show details for customer with Id: ' + customer.id);
      $state.go('customerDetails', {customerId: customer.id});
    };
    $scope.deleteCustomer = function (customer) {
      $log.info('Deleting customer with Id: ' + customer.id);
      $http.delete(appConfiguration.customerApiUrl + '/customers/' + customer.id).then(function() {
        $scope.loadCustomers();
      });
    };
    $scope.loadCustomers = function () {
      var customerPromise = $http.get(appConfiguration.customerApiUrl + '/customers');
      customerPromise.then(function(customers) {
        $log.info('Retrieved customers', customers);
        if (customers.data._embedded) {
          $scope.customers = customers.data._embedded.customers;
        }
      });
    };

    $scope.loadCustomers();
  })

  .controller('AddCustomerController', function ($scope, $state, $log, $http, appConfiguration) {
    $scope.map = {
        center: {
            latitude: 45,
            longitude: -73
        },
        zoom: 12,
        events: {
          tilesloaded: function (map, eventName, originalEventArgs) {
            $log.log('Map has loaded: ' + eventName, map, originalEventArgs);
          },
          click: function (mapModel, eventName, originalEventArgs) {
            // 'this' is the directive's scope
            $log.log('user defined event: ' + eventName, mapModel, originalEventArgs);

            var e = originalEventArgs[0];
            var lat = e.latLng.lat(),
                lon = e.latLng.lng();
            $scope.map.clickedMarker = {
              id: 0,
              title: 'You clicked here ' + 'lat: ' + lat + ' lon: ' + lon,
              latitude: lat,
              longitude: lon
            };
            $scope.customer.address.location.latitude = lat;
            $scope.customer.address.location.longitude = lon;
            //scope apply required because this event handler is outside of the angular domain
            $scope.$apply();
          }
       }
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
    $scope.geocodeAddress = function () {
      var geocoder = new google.maps.Geocoder();
      var address = [];

      if ($scope.customer.address.street) {
        address.push($scope.customer.address.street);
      }
      if ($scope.customer.address.city) {
        address.push($scope.customer.address.city);
      }
      if ($scope.customer.address.zipCode) {
        address.push($scope.customer.address.zipCode);
      }

      $log.info('Geocoding address:', address.join(','));
      geocoder.geocode( { 'address': address.join(',')}, function(results, status) {
        //if (status === google.maps.GeocoderStatus.OK) {
          $log.info('Found coordinates:', results[0].geometry.location);
          $scope.customer.address.location.latitude = results[0].geometry.location.lat;
          $scope.customer.address.location.longitude = results[0].geometry.location.lng;
          $scope.apply();
          // map.setCenter(results[0].geometry.location);
          // var marker = new google.maps.Marker({
          //   map: map,
          //   position: results[0].geometry.location
          // });
        //} else {
          //alert('Geocode was not successful for the following reason: ' + status);
        //}
      });
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
      var addCustomerPromise = $http.post(appConfiguration.customerApiUrl + '/customers', $scope.customer);

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
  .controller('CustomerDetailsController', function ($scope, $state, $stateParams, $http, $log, appConfiguration) {
    var customerId = $stateParams.customerId;
    var customerPromise = $http.get(appConfiguration.customerApiUrl + '/customers/' + customerId);
    
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
