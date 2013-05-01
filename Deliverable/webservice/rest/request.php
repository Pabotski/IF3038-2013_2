<?php
	function SendRequest( $url, $method = 'GET', $data = array(), $headers = array('Content-type: application/x-www-form-urlencoded') ) {
	$context = stream_context_create(array
	(
		'http' => array(
			'method' => $method,
			'header' => $headers,
			'content' => http_build_query( $data )
		)
	));
 
	return file_get_contents($url, false, $context, 1000000);
	}
?>