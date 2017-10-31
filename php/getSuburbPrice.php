<?php
//post method used
//check if recieved method is post

// array for JSON response
$response = array();

if($_SERVER["REQUEST_METHOD"]=="POST"){

        require 'dbConfig.php';
        getHouse();

}

function getHouse(){
	$NAME = $_POST["NAME"];
	
	global $connect;
	
	if (!$connect) {
    	die("Connection failed: " . mysqli_connect_error());
	}

	$sql = "SELECT * FROM SUBURBS WHERE SUBURB LIKE '$NAME'";
	//echo $sql;
	$result = mysqli_query($connect, $sql);

	if (mysqli_num_rows($result) > 0) {
	$SUBURB = array();
    // output data of each row
    	while($row = mysqli_fetch_assoc($result)) {
            //$SUBURB = array();
            $SUBURB["AVG_PRICE"] = $row["AVG_PRICE"];
    	    //echo "USERNAME: " . $row["USERNAME"]. " - PASSWORD: " . $row["PASSWORD"]. "<br>";
	    }
	    $response["success"] = 1;
        // user node
        $response["SUBURB"] = array();

        array_push($response["SUBURB"], $SUBURB);

        // echoing JSON response
	//print_r(json_encode($response));
        //echo("test");
	echo json_encode($response);
        
	} else {
	    // no users found
        $response["success"] = 0;
        $response["message"] = "No SUBURB found";
        // echo no users JSON
        echo json_encode($response);
	}

mysqli_close($connect);
}


?>
