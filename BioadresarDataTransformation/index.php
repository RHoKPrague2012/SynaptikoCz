<?php
header('Content-type: text/plain; charset=utf-8');
require_once('dibi.min.php');

// connect to database
dibi::connect(array(
    'driver'   => 'mysql',
    'host'     => 'localhost',
    'username' => 'duha',
    'password' => 'duha',
    'database' => 'hnutiduha6730'
));

$result = dibi::query('SELECT * FROM produkt WHERE je_kategorie = "ano"');
$insertCategoryPattern = "INSERT INTO category (_id, name) VALUES (%s, '%s');\n";

foreach ($result as $n => $row) {
  printf($insertCategoryPattern, $row['id'], $row['nazev']);
}
echo "\n";

$result = dibi::query('SELECT * FROM produkt WHERE NOT je_kategorie = "ano"');
$insertProductPattern = "INSERT INTO product (_id, name) VALUES (%s, '%s');\n";

foreach ($result as $n => $row) {
  printf($insertProductPattern, $row['id'], $row['nazev']);
}
echo "\n";

$result = dibi::query('SELECT divize_id, producent.nazev, latitude, longtitude, divize.poznamka, divize.typ FROM divize, producent, kontakt WHERE producent.id = divize.producent_id AND kontakt.divize_id = divize.id AND latitude IS NOT NULL ORDER BY divize.id');
$insertFarmPattern = "INSERT INTO farm (_id, name, gps_lat, gps_long, desc, type) VALUES (%s, '%s', %s, %s, %s, '%s');\n";
foreach ($result as $n => $row) {
  $poznamka = trim($row['poznamka']);
  printf($insertFarmPattern, $row['divize_id'], $row['nazev'], $row['latitude'], $row['longtitude'], (empty($poznamka) ? 'NULL' : "'$poznamka'"), $row['typ']);
}
echo "\n";

$result = dibi::query('SELECT divize_id, email, web, web_eshop, mobil, ulice, mesto, kraj, kraje.nazev FROM divize, kontakt, kraje WHERE kontakt.divize_id = divize.id AND latitude IS NOT NULL AND kontakt.kraj = kraje.kod ORDER BY divize.id');
$insertContactPattern = "INSERT INTO contact (farm_id, type, contact) VALUES (%s, '%s', '%s');\n";
foreach ($result as $n => $row) {
  if (!empty($row['email'])) {
    printf($insertContactPattern, $row['divize_id'], 'email', $row['email']);
  }
  if (!empty($row['web'])) {
    // TODO check if contains http:// and add it if not
    printf($insertContactPattern, $row['divize_id'], 'web', $row['web']);
  }
  if (!empty($row['web_eshop'])) {
    // TODO check if contains http:// and add it if not
    printf($insertContactPattern, $row['divize_id'], 'eshop', $row['web_eshop']);
  }
  if (!empty($row['mobil'])) {
    $mobil = $row['mobil'];
    $mobil = trim($mobil);
    
    // FIXME there can be some number which contains for example 311/22 or 177/188 at the end
    $pattern = '/(\d{3})\s?(\d{3})\s?(\d{3})/';
    preg_match_all($pattern, $mobil, $matches, PREG_SET_ORDER);
    foreach ($matches as $match) {
      printf($insertContactPattern, $row['divize_id'], 'phone', $match[0]);
    }
  }
  if (!empty($row['ulice'])) {
    printf($insertContactPattern, $row['divize_id'], 'street', $row['ulice']);
  }
  if (!empty($row['mesto'])) {
    printf($insertContactPattern, $row['divize_id'], 'city', $row['mesto']);
  }
  // we don't need it now :-)
  /*if (!empty($row['nazev'])) {
    printf($insertContactPattern, $row['divize_id'], 'region', $row['nazev']);
  }
  if (!empty($row['kraj'])) {
    printf($insertContactPattern, $row['divize_id'], 'regionCode', $row['kraj']);
  }*/
}
echo "\n";

$result = dibi::query('SELECT divize_id, produkt_id FROM produkuje, produkt WHERE produkt.id = produkuje.produkt_id AND produkt.je_kategorie = "ano" ORDER BY divize_id');
$insertFarmCategoryPattern = "INSERT INTO farm_category (farm_id, category_id) VALUES (%s, %s);\n";
foreach ($result as $n => $row) {
  printf($insertFarmCategoryPattern, $row['divize_id'], $row['produkt_id']);
}
echo "\n";

$result = dibi::query('SELECT divize_id, produkt_id FROM produkuje, produkt WHERE produkt.id = produkuje.produkt_id AND NOT produkt.je_kategorie = "ano" ORDER BY divize_id');
$insertFarmProductPattern = "INSERT INTO farm_product (farm_id, product_id) VALUES (%s, %s);\n";
foreach ($result as $n => $row) {
  printf($insertFarmProductPattern, $row['divize_id'], $row['produkt_id']);
}
echo "\n";
?>