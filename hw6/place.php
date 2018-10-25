<?php

// if (isset($_POST["search"])) {
if (isset($_POST["keyword"]) && !isset($_POST["id"])) {
  $location = $lon = $lat = $res = "";
  $cates = array("cafe", "bakery", "restaurant", "beauty salon", "casino", "movie theater", "lodging", "airport", "train station", "subway station", "bus station");

  $keyword = $_POST["keyword"];
  $category = $_POST["category"];
  $distance = (empty($distance)) ? 16093.44 : $_POST["distance"]*1609.344;

  function findNearby($lat, $lon, $distance, $category, $keyword) {
    //AIzaSyAEeGZZnhsgBGDfhnKBFADWWHT9qHb99Bg
    //AIzaSyD5-yOZ7ciEjzuqVi-_qic0TQsmdgUJ3mU
    $context = stream_context_create(array(
      'http' => array('ignore_errors' => true),
    ));

    $url_nearby = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=".$lat.",".$lon."&radius=".$distance."&type=".$category."&keyword=".$keyword."&key=AIzaSyAEeGZZnhsgBGDfhnKBFADWWHT9qHb99Bg";
    $req_nearby = file_get_contents($url_nearby, false, $context);
    return $req_nearby;
  }

  if (!empty($_POST["location"])) {
    $context = stream_context_create(array(
      'http' => array('ignore_errors' => true),
    ));

    $location = urlencode($_POST["location"]);
    // $apiKey = "AIzaSyAa82JVs4PT58-oozGACpFLBvuoQbA7IWM";
    $url = "https://maps.googleapis.com/maps/api/geocode/json?address=".$location."&key=AIzaSyAa82JVs4PT58-oozGACpFLBvuoQbA7IWM";
    $req = file_get_contents($url, false, $context);
    $loc = json_decode($req, true);
    $lon = $loc['results'][0]['geometry']['location']['lng'];
    $lat = $loc['results'][0]['geometry']['location']['lat'];
  }
  else {
    $lon = $_POST["lon"];
    $lat = $_POST["lat"];
  }
  if ($category != "default") {
    $res = findNearby($lat, $lon, $distance, $category, $keyword);
  }
  else {
    foreach ($cates as $c) {
      $tmp = findNearby($lat, $lon, $distance, $category, $keyword);
      if (empty($res)) {
        $res = $tmp;
      }
      else {
        $res = json_encode(array_merge(json_decode($res, true),json_decode($tmp, true)));
      }
    }
  }
  //AIzaSyBDg-ge5m-y2dPX1LuB1AGUOE7rz4FylDc
  // $res_return = json_decode($res);
  // AIzaSyAQ-E0FqVy7SUVVFIT7jXiu592D-ypLFJw
// https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=40.7127753,-74.0059728&radius=15&type=restaurant&keyword=pizza&key=AIzaSyBDg-ge5m-y2dPX1LuB1AGUOE7rz4FylDc%22
  $res_return = array("lat"=>$lat, "lng"=>$lon, "re"=>json_decode($res));
  echo json_encode($res_return);
  exit();
 }
 else if(isset($_POST["id"])) {
   $context = stream_context_create(array(
      'http' => array('ignore_errors' => true),
   ));

  $id = urlencode($_POST["id"]);
  $url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=".$id."&key=AIzaSyDLiZDLH2KMUskqxY8mwjXhOVZuYk3jxlQ";
  $req = file_get_contents($url, false, $context);
  $places = json_decode($req, true);
  $photos = [];
  if (isset($places['result']['photos'])) {
    $photos =  count($places['result']['photos']) > 5? array_slice($places['result']['photos'], 0, 5) : $places['result']['photos'];
  }
  // $photos = $places['result']['photos'];
  $i;
  // echo json_encode($photos);
  for ($i = 0; $i < count($photos); $i++) {
    $url_photo = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1600&photoreference=".urlencode($photos[$i]['photo_reference'])."&key=AIzaSyAoT08jmgZxcwI_FeX-VqKkr4fkMumHdVI";
    $tmp = file_get_contents($url_photo);
    file_put_contents("/home/scf-23/zhen114/apache2/htdocs/".$i.".jpg", $tmp);
  }
  while ($i < 5 and file_exists("/home/scf-23/zhen114/apache2/htdocs/".$i.".jpg")) {
    unlink("/home/scf-23/zhen114/apache2/htdocs/".$i.".jpg");
    $i++;
  }
  // $ind = null;
  // if ($photos != null) {
  //   // for ($j = 0; $j < count($places['result']['photos']); $j++) {
  //   //   $ind[$places['result']['photos'][$j][photo_reference]] =  $places['result']['photos'][$j][height] * $places['result']['photos'][$j][width];
  //   // }
  //   // foreach ($places['result']['photos'] as $e) {
  //   //   $ind[] = $e[height] * $e[width];
  //   // }
  //
  //   // arsort($ind);
  //   // echo json_encode($ind);
  //   $ind = count($ind) > 5? array_slice($ind, 0, 5) : $ind;
  //   $photo_return = array_keys($ind);
  //   for ($i = 0; $i < count($photo_return); $i++) {
  //     $url_photo = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1600&photoreference=".urlencode($photo_return[$i])."&key=AIzaSyAoT08jmgZxcwI_FeX-VqKkr4fkMumHdVI";
  //     $tmp = file_get_contents($url_photo);
  //     file_put_contents($i.".jpg", $tmp);
  //   }
  // }

  // for ($i = 0; $i < count($ind) && $i < 5; $i++) {
  //
  // }
  $reviews = [];
  if (isset($places['result']['reviews'])) {
    $reviews =  count($places['result']['reviews']) > 5 ? array_slice($places, 0, 5) : $places['result']['reviews'];
  }
  // $res = array("reviews"=>$reviews, "photos"=>$photo_return);

  echo json_encode($reviews);
  exit();
}
// echo $res;
// echo $lon;
// echo $lat;
// }
?>



<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Travel and Entertainment Search</title>
    <style>
       form {
        width: 600px;
        margin: auto;
        background-color: rgb(250, 250, 250)
      }
      #head {
        font-style: italic;
        text-align: center;
        font-size: 36px;
        margin-top: 0;
        margin-bottom: 0;
        font-size: 30px;
      }
      #radioes {
        position: relative;
        left: 300px;
        top: -20px;
      }
      #buttons {
        margin-left: 60px;
      }
    </style>
  </head>
  <body>
    <form method="POST" action="place.php" id="travel" onsubmit="return ajaxPost();">
      <fieldset id="field">
        <p id="head">Travel and Entertainment Search</p>
        <hr>
        <b>Keyword</b> <input type="text" name="keyword" id="keyword" required> <br>
        <!-- <b>Keyword</b> <input type="text" name="keyword" id="keyword" onkeyup="saved(this);" required> <br> -->
        <b>Category</b> <select name="category" id="category">
          <option value="default" selected>default</option>
          <option value="cafe">cafe</option>
          <option value="bakery">bakery</option>
          <option value="restaurant">restaurant</option>
          <option value="beauty salon">beauty salon</option>
          <option value="casino">casino</option>
          <option value="movie theater">movie theater</option>
          <option value="lodging">lodging</option>
          <option value="airport">airport</option>
          <option value="train station">train station</option>
          <option value="subway station">subway station</option>
          <option value="bus station">bus station</option>
        </select><br>
        <b>Distance(miles)</b> <input type="text" name="distance" placeholder=10 id="distance">
        <b>from</b>
        <div id="radioes">
          <input onclick="unselected()" type="radio" name="location" value="here" id="loc1" checked>Here<br>
          <input onclick="selected()" type="radio" name="location" value="" id="loc2"><input type="text" name="custom" id="loc3" placeholder="location" required disabled>
        </div>
        <div id="buttons">
          <input type="submit" name="search" value="Search" id="search" disabled>
          <input type="button" name="clear" value="Clear" onclick="clearForm()">
        </div>
        <input type="hidden" name="lat" id="lat">
        <input type="hidden" name="lon" id="lon"><br>
      </fieldset>
    </form>
    <div id="result"></div>
  </body>
</html>

<script async defer
  src="https://maps.googleapis.com/maps/api/js?key=AIzaSyA4wNdxWk5_jF1xUvlmKtrB3xcuDCsjcDE">;
</script>
<script text = "text/javascript">
  var long;
  var lati;
  window.onload = function() {
    var myForm = document.getElementById("travel");
    // formData = new FormData(myForm);
    var xhr = new XMLHttpRequest();
    // xhr.open("GET","//ipinfo.io/json", false);
    xhr.open("GET","//ip-api.com/json?callback=", false);
    xhr.send(null);
    var res = xhr.response;
    content = JSON.parse(res);
    // console.log(content);
    // long = content.lon;
    // lati = content.lat;
    // var locs = (content.loc).split(",");
    // console.log(locs);
    // document.getElementById("lat").value = locs[0];
    // document.getElementById("lon").value = locs[1];
    document.getElementById("lat").value = content.lat;
    document.getElementById("lon").value = content.lon;
    // document.getElementById("keyword").value = getSaved("keyword");
    // document.getElementById("category").value = getSaved("category");
    // console.log(getSaved("category"));
    // document.getElementById("distance").value = getSaved("distance");
    // document.getElementById("keyword").value = getSaved("keyword");
    // console.log(long);
    // console.log(lati);
    // console.log(url);
    // console.log(document.getElementById("travel")["action"]);
    // document.getElementById("travel")["action"].value = url;
    // formData.append('lon', long);
    // formData.append('lat', lati);
    // console.log(formData['lon']);
    // console.log(formData['lat']);
    loaded();
  }
  //  if (submit != "") {
  // //   var leng = (res["results"]).length;
  //   console.log("hi");

  // }
  // function retain() {
  //   document.getElementById("keyword").value = getSaved("keyword");
  // }
  function ajaxPost() {
    var xhr = null;
    if (window.XMLHttpRequest) {
      xhr = new XMLHttpRequest();
    }
    else if (window.ActiveXObject) {
      xhr = new ActiveXObject("Microsoft.XMLHTTP");
    }
    if (xhr == null) {
      alert("Your browser does not support XMLHTTP.");
    }
    else {
      xhr.overrideMimeType("application/json");
      var key = document.getElementById("keyword").value;
      var cateS = document.getElementById("category");
      var cate = cateS.options[cateS.selectedIndex].value;
      var dis = document.getElementById("distance").value;
      var loc = document.getElementById("loc3").value;
      var lon = document.getElementById("lon").value;
      var lat = document.getElementById("lat").value;
      var vars = "keyword="+key+"&category="+cate+"&distance="+dis+"&location="+loc+"&lon="+lon+"&lat="+lat;
      // console.log(vars);
      xhr.open("POST", "place.php", true);
      xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
      xhr.onreadystatechange = function() {
        if (xhr.readyState == 4 && xhr.status == 200) {
          // console.log(xhr.responseText);
          var data = JSON.parse(xhr.responseText);
          latitude = Number(data["lat"]);
          longitude = Number(data["lng"]);
          getTable(data["re"]);
        }
      }
      xhr.send(vars);
      return false;
    }
  }
  function getTable(data) {
    // console.log(data);
    var re = document.getElementById("result");
    re.style.marginTop = "20px";
    // console.log(data.results);
    // console.log(data.results.length);
    if ((data["results"]).length == 0) {
      re.innerHTML = "No Records has been found";
      re.style.width = "700px";
      re.style.backgroundColor = "#efefef";
      re.style.border = "medium solid #e4e4e4";
      re.style.margin = "auto";
      re.style.textAlign = "center";
      re.style.marginTop = "20px";
    }
    else {
      var text = '<table style="width: 1000px; border: medium solid #E8E8E8; border-collapse: collapse" align="center"  ;>';
      // text += "<tr><th>Category</th><th>Name</th><th>Address</th>";
      var heads = ["Category", "Name", "Address"];
      for (var e of heads) {
        text += '<td style = "border: medium solid #E8E8E8; border-collapse: collapse; text-align: center; font-weight: bold">'+e+"</td>";
      }
      var leng = data.results.length;
      resu = data.results;
      for (i = 0; i < leng; i++) {
        var tmp = data.results[i];
        text += '<tr id="'+i+'" style= \"height: 40px\">';
        // console.log(data.results.(this.rowIndex).place_id);
        text += '<td style = \"border: medium solid #E8E8E8; width: 70px;  text-align: left; border-collapse: collapse; height: 40px\"><img src='+tmp.icon+' alt=\"category_pic\";  style=\"height: 40px\"></td>';
        text += '<td style = "border: medium solid #E8E8E8; width: 550px; text-align: left; border-collapse: collapse; height: 40px; cursor: pointer" onclick="review(this.parentNode.id)">' + tmp.name + '</td>';
        // text += '<td id='+i+'style = "border: medium solid #E8E8E8; border-collapse: collapse; height: 30px; cursor: pointer" onclick="reviews('+this.id+')">'+tmp.name+"</td>";
        text += '<td style = \"border: medium solid #E8E8E8; width: 500px; text-align: left; border-collapse: collapse; height: 40px; cursor: pointer\" onclick="showMap(this.parentNode.id)">'+tmp.vicinity+'</td>';
        // text += '<p  style = \"cursor: pointer\" onclick="showMap(this.parentNode.parentNode.id)">'+tmp.vicinity+'</p>';
        // text +=
        text += "</tr>";

        // console.log(this.rowIndex);
      }
      text += "</table>";
      text += '<div id="map_t">';
      text += '<div id="map_d"></div>';
      text += '<div id="mode">';
      text += '<div id="walk" class="traffic"></div>';
      text += '<div id="bike" class="traffic"></div>';
      text += '<div id="drive" class="traffic"></div>';
      text += '</div>';
      text += '</div>';
      re.innerHTML = text;
      // document.getElementsByTagName("td").style.border = "1px solid grey";
    }
  }
  // function getLeft(x) {
  //   var tmp = x.offsetLeft;
  //   if (x.offsetParent != null) {
  //     tmp += getLeft(x.offsetParent);
  //   }
  //   return tmp;
  // }
  // function getTop(x) {
  //   var tmp = x.offsetTop;
  //   if (x.offsetParent != null) {
  //     tmp += getTop(x.offsetParent);
  //   }
  //   return tmp;
  // }
  function showMap(i) {
    // var cl = document.getElementById("words");
    // console.log(i);
    var id = Number(i);
    // console.log(id);
    var x = document.getElementsByTagName("td");
    // console.log(id);
    var cl = x[(id+1)*3+2];
    // console.log((id+1)*3+2);
    // console.log(cl.innerHTML);
    document.getElementById("walk").innerHTML="Walk there";
    document.getElementById("bike").innerHTML="Bike there";
    document.getElementById("drive").innerHTML="Drive there";
    var mat = document.getElementById("map_t");
    mat.style.width = "300px";
    mat.style.height = "300px";
    mat.style.position = "absolute";
    // console.log(getLeft(cl));
    // console.log(getTop(cl));
    // console.log(cl.offsetLeft+150);
    // console.log(cl.offsetTop+275);
    // mat.style.left = getLeft(cl)+"px";
    // mat.style.left = getTop(cl)+"px";
    mat.style.left = (cl.offsetLeft+150)+"px";
    mat.style.top = (cl.offsetTop+275)+"px";
    mat.style.zIndex = "1";
    // mat.style.border = "thin solid black";
    var ma = document.getElementById("map_d");
    ma.margin = "0";
    ma.style.width = "100%";
    ma.style.height = "100%";
    ma.style.zIndex = "2";
    // ma.style.position = "relative";
    // ma.style.top = "-47px";
    var mode = document.getElementById("mode");
    mode.style.zIndex = "100";
    // mode.style.float = "left";
    // mode.style.border = "thin solid black";
    mode.style.position = "relative";
    mode.style.top = "-300px";
    // mode.style.backgroundColor = "#E8E8E8";
    // mode.style.height = "90px";
    mode.style.width = "80px";
    var walk = document.getElementById("walk");
    var bike = document.getElementById("bike");
    var drive = document.getElementById("drive");
    walk.style.height = "30px";
    walk.style.textAlign = "center";
    walk.style.cursor = "pointer";
    walk.style.backgroundColor = "#E8E8E8";
    // walk.style.verticalAlign = "middle";
    bike.style.height = "30px";
    bike.style.textAlign = "center";
    bike.style.cursor = "pointer";
    bike.style.backgroundColor = "#E8E8E8";
    // bike.style.verticalAlign = "middle";
    drive.style.height = "30px";
    drive.style.textAlign = "center";
    drive.style.cursor = "pointer";
    drive.style.backgroundColor = "#E8E8E8";
    // drive.style.verticalAlign = "middle";
    // ma.style.position = "absolute";
    // ma.style.left = (cl.offsetLeft+150)+"px";
    // ma.style.top = (cl.offsetTop+273)+"px";
    // ma.style.zIndex = "1";
    // console.log(cl.offsetLeft);
    // console.log(cl.offsetTop);
    var dest_lng = resu[id]["geometry"]["location"]["lng"];
    var dest_lat = resu[id]["geometry"]["location"]["lat"];
    var dest = {lat: dest_lat, lng: dest_lng};
    // console.log(dest);
    var map = new google.maps.Map(document.getElementById("map_d"), {
      zoom: 13,
      center: dest
    });
    // console.log(map);
    var marker = new google.maps.Marker({
      position: dest,
      map: map
    });
    // console.log(dest_lat);
    // console.log(dest_lng);
    // console.log(latitude);
    // console.log(longitude);
    // console.log((dest_lat+latitude)/2);
    // console.log((dest_lng+longitude)/2);
    walk.onclick = function() {travel(dest_lat, dest_lng, "WALKING")};
    bike.onclick = function() {travel(dest_lat, dest_lng, "BICYCLING")};
    drive.onclick = function() {travel(dest_lat, dest_lng, "DRIVING")};
    var count = (x.length+1)/3-2;
    for (var j = 0; j <= count; j++) {
      if (j != id) {
        var temp = x[(j+1)*3+2];
        // console.log(j);
        // console.log((j+1)*3+2);
        // console.log(temp.innerHTML);
        // temp.onclick = function() {showMap(j)};
        temp.onclick = (function(j){
          return function(){
            showMap(j);
          }
        })(j);
      }
    }
    cl.onclick = function() {hideMap(id)};
    // console.log("show");
    // var x = document.getElementById("words").innerHTML;
    // var text = '<p style = "cursor: pointer" onclick="hideMap('+id+')">'+x+'</p>';
    // text += '<div id="maps">show</div>';
    // ma.innerHTML = text;
  }
  function travel(dest_lat, dest_lng, mode) {
    var directionsDisplay = new google.maps.DirectionsRenderer;
    var directionsService = new google.maps.DirectionsService;
    var map = new google.maps.Map(document.getElementById('map_d'), {
      zoom: 14,
      center: {lat: (dest_lat+latitude)/2, lng: (dest_lng+longitude)/2}
    });
    directionsDisplay.setMap(map);
    calculateAndDisplayRoute(directionsService, directionsDisplay, mode, dest_lat, dest_lng);
  }
  function calculateAndDisplayRoute(directionsService, directionsDisplay, mode, dest_lat, dest_lng) {
    directionsService.route({
      origin: {lat: latitude, lng: longitude},
      destination: {lat: dest_lat, lng: dest_lng},
      travelMode: google.maps.TravelMode[mode]
    }, function(response, status) {
      if (status == 'OK') {
        directionsDisplay.setDirections(response);
      } else {
        window.alert('Directions request failed due to ' + status);
      }
    });
  }
  function hideMap(i) {
    var id = Number(i);
    // var cl = document.getElementById("words");
    var x = document.getElementsByTagName("td");
    var cl = x[(id+1)*3+2];
    // console.log(cl.innerHTML);
    var ma = document.getElementById("map_d");
    ma.innerHTML = "";
    ma.style.zIndex = "-1";
    var mat = document.getElementById("map_t");
    mat.style.zIndex = "-1";
    var mode = document.getElementById("mode");
    mode.style.zIndex = "-1";
    document.getElementById("walk").innerHTML="";
    document.getElementById("bike").innerHTML="";
    document.getElementById("drive").innerHTML="";
    document.getElementById("walk").style.backgroundColor = "white";
    document.getElementById("bike").style.backgroundColor = "white";
    document.getElementById("drive").style.backgroundColor = "white";
    var count = (x.length+1)/3-2;
    // for (var j = 0; j <= count; j++) {
    //   // if (j != id) {
    //     x[(j+1)*3+2].onclick = function() {showMap(j)};
    //   // }
    // }
    cl.onclick = function() {showMap(id)};
    // console.log("hide");
    // var x = document.getElementById("words").innerHTML;
    // var text = '<p style = "cursor: pointer" onclick="showMap('+id+')">'+x+'</p>';
    // text += '<div id="maps">hide</div>';
    // ma.innerHTML = text;
  }
  function review(x) {
    var id = resu[x]["place_id"];
    var name = resu[x]["name"];
    // console.log(id);
    var xhr = null;
    if (window.XMLHttpRequest) {
      xhr = new XMLHttpRequest();
    }
    else if (window.ActiveXObject) {
      xhr = new ActiveXObject("Microsoft.XMLHTTP");
    }
    if (xhr == null) {
      alert("Your browser does not support XMLHTTP.");
    }
    else {
      xhr.overrideMimeType("application/json");
      var vars = "id="+id;
      xhr.open("POST", "place.php", true);
      xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
      xhr.onreadystatechange = function() {
        if (xhr.readyState == 4 && xhr.status == 200) {
          // console.log(xhr.responseText);
          reviews = JSON.parse(xhr.responseText);
          // console.log(place.photos);
          // console.log(place.reviews);
          getPhotoAndReview(name);
        }
      }
      xhr.send(vars);
    }
  }
  function getPhotoAndReview(name) {
    var re = document.getElementById("result");
    var text = "<div><b>"+name+"</b></div>";
    text += "<div id=\"rev\">";
    text += "</div>";
    text += "<div id=\"pho\">"
    text += "</div>";
    re.innerHTML = text;
    hideReviews();
    hidePhotos();
    re.style.textAlign = "center";
    document.getElementById("rev").marginTop = "10px";
    // var photos = place.result.photos;
    // var reviews = place.result.reviews;
  }
  function hideReviews() {
    var rev = document.getElementById("rev");
    var text = "click to show reviews<br>";
    text += "<img id=\"revImg\" src=\"http://cs-server.usc.edu:45678/hw/hw6/images/arrow_down.png\" alt=\"down\" height=\"20px\" width=\"30px\" style=\"cursor: pointer\" onclick=showReviews()>";
    rev.innerHTML = text;
    // document.getElementById("revImg").removeEventListener("click", hideReviews(reviews), false);
    // document.getElementById("revImg").addEventListener("click", showReviews(reviews), false);
  }
  function hidePhotos() {
    var pho = document.getElementById("pho");
    var text = "click to show photos<br>";
    text += "<img id=\"phoImg\" src=\"http://cs-server.usc.edu:45678/hw/hw6/images/arrow_down.png\" alt=\"down\" height=\"20px\" width=\"30px\" style=\"cursor: pointer\" onclick=showPhotos()>";
    pho.innerHTML = text;
    // ocument.getElementById("revImg").removeEventListener("click", hidePhotos(reviews), false);
    // document.getElementById("phoImg").addEventListener("click", showPhotos(photos), false);
  }
  function showReviews() {
    var rev = document.getElementById("rev");
    var text = "click to hide reviews<br>";
    text += "<img id=\"revImg\" src=\"http://cs-server.usc.edu:45678/hw/hw6/images/arrow_up.png\" alt=\"up\" height=\"20px\" width=\"30px\" style=\"cursor: pointer\" onclick=hideReviews()>";
    var count = 0;
    for (x in reviews) {
      count++;
    }
    text += "<table style=\"width: 600px; border: medium solid #E8E8E8; border-collapse: collapse\" align=\"center\">";
    if (count == 0) {
      text += "<tr>";
      text += '<td style = \"border: medium solid #E8E8E8; border-collapse: collapse; height: 30px; font-weight: bold\">No Reviews Found</td>';
      text += "</tr>";
    }
    else {
      // console.log(reviews);
      for (x in reviews) {
        // console.log(reviews[x]);
        // console.log(reviews[x]["author_name"]);
        text += "<tr>";
        text += '<td style = \"border: medium solid #E8E8E8; border-collapse: collapse; height: 30px; font-weight: bold\"><img src='+reviews[x]["profile_photo_url"]+' alt=\"profile_pic\" width=\"30px\"></img>'+reviews[x]["author_name"]+'</td>';
        // text += '<td style = \"border: medium solid #E8E8E8; border-collapse: collapse; height: 30px; font-weight: bold\">'+reviews[x]["author_name"]+'</td>';
        text += "</tr>";
        text += "<tr>";
        text += '<td style = \"border: medium solid #E8E8E8; border-collapse: collapse; height: 30px; text-align: left\">'+reviews[x]["text"]+'</td>';
        text += "</tr>";
      }
    }
    text += "</table>";
    rev.innerHTML = text;
    hidePhotos();
    // ocument.getElementById("revImg").removeEventListener("click", showReviews(reviews), false);
    // document.getElementById("revImg").addEventListener("click", hideReviews(reviews), false);
  }
  function showPhotos() {
    var d = new Date();
    var pho = document.getElementById("pho");
    var text = "click to hide photos<br>";
    text += "<img id=\"phoImg\" src=\"http://cs-server.usc.edu:45678/hw/hw6/images/arrow_up.png\" alt=\"up\" height=\"20px\" width=\"30px\"  style=\"cursor: pointer\"  onclick=hidePhotos()>";
    text += "<table style=\"width: 600px; border: medium solid #E8E8E8; border-collapse: collapse\" align=\"center\">";
    var count = 0;
    var url = count+".jpg";
    while (imageExist(url)) {
      text += "<tr>";
      text += "<td style = \"border: medium solid #E8E8E8; border-collapse: collapse\">";
      text += '<a href="'+url+'?ver='+d.getTime()+'" target=\"_blank\">';
      text += "<img src="+url+"?ver="+d.getTime()+" alt=\"photo_pic\" width=\"600px\" height=\"500px\" style=\"cursor: pointer\">";
      text += '</a>';
      text += '</td>';
      text += "</tr>";
      count++;
      url = count+".jpg";
    }
    if (count == 0) {
      text += "<tr>";
      text += '<td style = \"border: medium solid #E8E8E8; border-collapse: collapse; height: 30px; font-weight: bold\">No Photos Found</td>';
      text += "</tr>";
    }
    text += "</table>";
    pho.innerHTML = text;
    hideReviews();
    // ocument.getElementById("revImg").removeEventListener("click", showPhotos(reviews), false);
    // document.getElementById("phoImg").addEventListener("click", hidePhotos(photos), false);
  }
  function imageExist(url) {
    var http = new XMLHttpRequest();
    http.open('HEAD', url, false);
    // http.onreadystatechange = function() {
    //   if (http.status == 404) {
    //     return false;
    //   }
    //   else {
    //     return true;
    //   }
    // }
    http.send();
    return http.status != 404;
  }
  // function saved(ele) {
  //   var id = ele.id;
  //   var value = ele.value;
  //   window.localStorage.setItem(id, value);
  // }
  // function getSaved(ele) {
  //   if (window.localStorage.getItem(ele) == null) {
  //     return document.getElementById(ele).defaultValue;
  //   }
  //   return window.localStorage.getItem(ele);
  // }
  // function sub() {
  //   var xhr = new XMLHttpRequest();
  //   xhr.open("POST","place.php", false);
  //   var res = xhr.responseText;
  //   console.log(res);
  //   content = JSON.parse(res);
  //   content["lat"] = lati;
  //   content["lon"] = long;
  //   console.log(content);
  //   xhr.send(content);
  // //   return false;
  // //   // var myForm = document.getElementById("travel");
  // //   // var formData = new FormData(myForm);
  // //   // formData.append('lon', long);
  // //   // formData.append('lat', lati);
  // //   // console.log(formData);
  // //   // xhr.send(formData);
  // }
  function loaded() {
    document.getElementById("search").disabled = false;
  }
  function selected() {
    document.getElementById("loc3").disabled = false;
  }
  function unselected() {
    document.getElementById("loc3").disabled = true;
  }
  function clearForm() {
    document.getElementById("travel").reset();
    // document.getElementById("keyword").value = document.getElementById("keyword").defaultValue;
    // document.getElementById("category").value = document.getElementById("category").defaultValue;
    // document.getElementById("distance").value = document.getElementById("distance").defaultValue;
    // document.getElementById("loc1").value = document.getElementById("loc1").defaultValue;
    // document.getElementById("loc2").value = document.getElementById("loc2").defaultValue;
    // document.getElementById("loc3").value = document.getElementById("loc3").defaultValue;
  }


</script>
<!-- 1.reset button needs to be done with javascript -
     2.map location adjustment -

     4.change the path of the read images
     5.change "addtype" of httpd.conf
     6.change the image format allowed by nginx
-->
