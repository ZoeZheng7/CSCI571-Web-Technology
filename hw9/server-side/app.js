var express = require('express');
var app = express();
var bodyParser = require('body-parser');
const https = require('https');
var url = require('url');
var EventEmitter = require("events").EventEmitter;
var request = require('request');
'use strict';



app.use(express.static(__dirname));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: false}));

app.get('/map', (req, res) => {
  res.setHeader('Content-Type', "application/json");
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "X-Requested-With");
  var bd = new EventEmitter();
  var start = req.query.start;
  var endLat = req.query.endLat;
  var endLng = req.query.endLng;
  var mode = req.query.mode;
  mode = mode.toLowerCase();
  // console.log(start, endLat, endLng, mode);
  var url = "https://maps.googleapis.com/maps/api/directions/json?origin="+start+"&destination="+endLat+","+endLng+"&mode="+mode+"&key=AIzaSyBZ9upjFvGYj0Jd6Ms0qdvMIduzqi_gg1E";
  https.get(url, resp => {
    resp.setEncoding("utf8");
    var html = "";
    resp.on("data", data => {
      html += data;
    });
    resp.on("end", () => {
      html = JSON.parse(html);
      console.log(html);
      bd.result = html;
      bd.emit('update');
    })
  })
  bd.on('update', function() {
    res.send(bd.result);
  })
})

app.get('/details', (req, res) => {
  res.setHeader('Content-Type', "application/json");
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "X-Requested-With");
  var bd = new EventEmitter();
  var id = req.query.id;
  // console.log(req.query.id);
  var url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+id+"&key=AIzaSyDLiZDLH2KMUskqxY8mwjXhOVZuYk3jxlQ";
  https.get(url, resp => {
    resp.setEncoding("utf8");
    var html = "";
    resp.on("data", data => {
      html += data;
    });
    resp.on("end", () => {
      html = JSON.parse(html);
      // console.log(html.results);
      bd.result = html;
      bd.emit('update');
    })
  })
  bd.on('update', function() {
    res.send(bd.result);
  })
  // res.send('get');
})

app.get('/next', (req, res) => {
  res.setHeader('Content-Type', "application/json");
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "X-Requested-With");
  var bd = new EventEmitter();
  // console.log(req.query);
  var token = req.query.token;
  // console.log(token);
  var url3 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken="+token+"&key=AIzaSyAEeGZZnhsgBGDfhnKBFADWWHT9qHb99Bg";
  https.get(url3, res3 => {
    // console.log(url3);
    res3.setEncoding("utf8");
    // res3.setHeader('Content-Type', "application/json");
    // res3.header("Access-Control-Allow-Origin", "*");
    // res3.header("Access-Control-Allow-Headers", "X-Requested-With");
    var html = "";
    var tmp = {};
    res3.on("data", data => {
      html += data;
      // console.log(data);
    });
    res3.on("end", () => {
      html = JSON.parse(html);
      var resu = html.results;
      var length = resu.length;
      // result['token'] = "";
      tmp['places'] = [];
      tmp['token'] = "";
      if (html.next_page_token != 'undefined') {
        tmp['token'] = html.next_page_token;
      }
      for (var i = 0; i < length; i++) {
        tmp.places[i] = {};
        tmp.places[i]['icon'] = resu[i]['icon'];
        tmp.places[i]['name'] = resu[i]['name'];
        tmp.places[i]['vicinity'] = resu[i]['vicinity'];
        tmp.places[i]['place_id'] = resu[i]['place_id'];
        tmp.places[i]['location'] = resu[i]['geometry']['location'];
      }
      bd.result = tmp;
      bd.emit('update');
    })
  })
  bd.on('update', function() {
    res.send(bd.result);
  })
})

app.get('/yelp', (req, res) => {
  res.setHeader('Content-Type', "application/json");
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "X-Requested-With");
  var bd = new EventEmitter();
  var que = req.query;
  var name = que.name;
  var addr1 = que.address1;
  var addr2 = que.address2;
  var city = que.city;
  var state = que.state;
  var country = que.country;
  var headers = {
    // 'Access-Control-Allow-Origin': 'http://localhost:8081/',
    'Authorization': 'Bearer DxBxlcE83XeIjo1nEtBN_i68bRAO6FGDdOj5Gegl6OB2QuWbMeWItL_TG6vMBhh8WP7lOUU7664UV3M304Xarp-R27Gl6g1UzZZ5QPoLl7HrWrKgT6d-8Wpp3UHEWnYx'
  };

  var apiKey = "DxBxlcE83XeIjo1nEtBN_i68bRAO6FGDdOj5Gegl6OB2QuWbMeWItL_TG6vMBhh8WP7lOUU7664UV3M304Xarp-R27Gl6g1UzZZ5QPoLl7HrWrKgT6d-8Wpp3UHEWnYx";
  const yelp = require('yelp-fusion');

  const client = yelp.client(apiKey);
  client.businessMatch('best', {
    name: name,
    address1: addr1,
    address2: city+', '+addr2,
    city: city,
    state: state,
    country: country
  }).then(response => {
    // response.setHeader('Content-Type', "application/json");
    // response.header("Access-Control-Allow-Origin", "*");
    // response.header("Access-Control-Allow-Headers", "X-Requested-With");
    // console.log(response.jsonBody.businesses);
    // console.log(response);
    if (response.jsonBody.businesses.length != 0) {
      yelpReviews(response.jsonBody.businesses[0].id);
    }
    else {
      res.send([]);
    }
  }).catch(e => {
    res.send("error");
  });
  function yelpReviews(id) {
    const yelpR = require('yelp-fusion');
    const clientR = yelpR.client(apiKey);
    clientR.reviews(id).then(responseR => {
      // console.log(responseR);
      // responseR.setHeader('Content-Type', "application/json");
      // responseR.header("Access-Control-Allow-Origin", "*");
      // responseR.header("Access-Control-Allow-Headers", "X-Requested-With");
    // console.log(responseR.jsonBody.reviews);
    bd.result = responseR.jsonBody.reviews;
    // console.log(bd.result);
    bd.emit('update');
    }).catch(e => {
      // console.log(e);
      res.send(JSON.stringify("error"));
    });
  }
  bd.on('update', function() {
    res.send(bd.result);
  })
})

app.get('/messages', (req, res) => {
  // console.log(req.body)
  // var que = url.parse(req.url, true).query;
  res.setHeader('Content-Type', "application/json");
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "X-Requested-With");
  var que = req.query;
  var keyword = que.keyword;
  var category = que.category.toLowerCase();
  var distance = (que.distance == 'undefined') ? 16093.44 : que.distance*1609.344;
  var location = que.location;
  var lon = que.lon;
  var lat = que.lat;
  var bd = new EventEmitter();
  // console.log(location);
  // console.log(lon);
  // console.log(lat);
  // var ob = null;
  if (location != "undefined") {
    // console.log("unde");
    unde(distance, category, keyword);
  }
  else {
    // console.log("de");
    de(lat, lon, distance, category, keyword);
  }

  function de(lat, lon, distance, category, keyword) {
    var url2 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat+","+lon+"&radius="+distance+"&type="+category+"&keyword="+keyword+"&key=AIzaSyAEeGZZnhsgBGDfhnKBFADWWHT9qHb99Bg";
    // var url2 = "https://maps.googleapis.com/maps/api/geocode/json?address="+location+"&key=AIzaSyAa82JVs4PT58-oozGACpFLBvuoQbA7IWM";
    https.get(url2, res2 => {
      // console.log(url2);
      res2.setEncoding("utf8");
      // res2.setHeader('Content-Type', "application/json");
      // res2.header("Access-Control-Allow-Origin", "*");
      // res2.header("Access-Control-Allow-Headers", "X-Requested-With");
      var rr = "";
      res2.on("data", data => {
        rr += data;
      });
      res2.on("end", () => {
        // console.log(rr);
        rr = JSON.parse(rr);
        var resu = rr.results;
        var length = resu.length;
        var result = [];
        result = {};
        result['token'] = "";
        result['places'] = [];

        if (rr.next_page_token != 'undefined') {
          result['token'] = rr.next_page_token;
        }
        for (var i = 0; i < length; i++) {
          result.places[i] = {};
          result.places[i]['icon'] = resu[i]['icon'];
          result.places[i]['name'] = resu[i]['name'];
          result.places[i]['vicinity'] = resu[i]['vicinity'];
          result.places[i]['place_id'] = resu[i]['place_id'];
          result.places[i]['location'] = resu[i]['geometry']['location'];
        }
        // console.log(result);
        bd.result = result;
        bd.emit('update');
      })
    })
  }

  function unde(distance, category, keyword) {
    var url1 = "https://maps.googleapis.com/maps/api/geocode/json?address="+location+"&key=AIzaSyAa82JVs4PT58-oozGACpFLBvuoQbA7IWM";
    https.get(url1, res1 => {
      // res1.setHeader('Content-Type', "application/json");
      // res1.header("Access-Control-Allow-Origin", "*");
      // res1.header("Access-Control-Allow-Headers", "X-Requested-With");
      // console.log(res1);
      res1.setEncoding("utf8");
      var body = "";
      res1.on("data", data => {
        body += data;
      });
      res1.on("end", () => {
        body = JSON.parse(body);
        // console.log(body);
        var lon = body.results[0].geometry.location.lng;
        var lat = body.results[0].geometry.location.lat;
        de(lat, lon, distance, category, keyword);
      })
    })
  }

  bd.on('update', function() {
    res.send(bd.result);
  })

})


var server = app.listen(8081, () => {
  console.log('server is listening on port', server.address().port);
})
