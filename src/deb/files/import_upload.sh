#!/bin/bash

entities=("" "kulturobjektdokument" "beschreibung" "digitalisat" "katalog")

uploadFile()
{
	uploadUrl="$2/$3/import$4"
	echo "Starte Upload von '$1' nach '${uploadUrl}'"
	statusCode=$(curl -X POST -H "Content-Type: multipart/form-data" -F "datei=@$1" -s -o /dev/null -w "%{http_code}" ${uploadUrl})
	echo "StatusCode für Upload von '$1' nach '${uploadUrl}': ${statusCode}"
	if [ "${statusCode}" != "201" ]; then
		echo "Upload ist fehlgeschlagen mit StatusCode ${statusCode}"
		return "1"
	fi
	return "0"
}

echo "URL der Import-Service REST-API: (z.B.: http://localhost:9296/rest)"
#read baseUrl
baseUrl="http://localhost:9296/rest"
echo "URL der Import-Service REST-API ist '${baseUrl}'"

echo "Nummer der Entität für den Import:"

for ((i=1; i<${#entities[@]}; i++)); do
    echo "$i: ${entities[$i]}"
done
read entitiesIdx

if [ "$entitiesIdx" -eq 1 ];
then
  importFolder="/opt/tei-odd/nachweis_daten/3_0_Produktion/1_Neue_KODs"
elif [ "$entitiesIdx" -eq 2 ];
then
  importFolder="/opt/tei-odd/nachweis_daten/3_0_Produktion"
elif [ "$entitiesIdx" -eq 3 ];
then
  importFolder="/opt/tei-odd/nachweis_daten/3_0_Produktion"
elif [ "$entitiesIdx" -eq 4 ];
then
  importFolder="/opt/tei-odd/nachweis_daten/4_0_Kataloge"
fi

echo "Import Ordner ist '${importFolder}'"

for file in $(find "${importFolder}" -name "0${entitiesIdx}_*.xml");  do
  internExtern=""
  if [[ $entitiesIdx = 2 && ${file} == *"_INT"* ]]
  then
    internExtern="/INTERN"
  elif [[ $entitiesIdx = 2 && ${file} == *"_EXT"* ]]
  then
    internExtern="/EXTERN"
  fi
  uploadFile "${file}" "${baseUrl}" "${entities[$entitiesIdx]}" "${internExtern}"
	result=$?
	if [ "$result" != 0 ]; then
		break;
	fi
done
