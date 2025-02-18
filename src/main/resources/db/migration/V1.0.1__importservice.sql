create table if not exists schema_resource_file
(
    id                  varchar(255) not null
        constraint schema_resource_file_pkey
            primary key,
    aenderungs_datum    timestamp,
    bearbeitername      varchar(64)  not null,
    datei_name          varchar(256) not null,
    erstellungs_datum   timestamp,
    schema_resource_typ varchar(32)  not null,
    version             varchar(50)  not null,
    xml_format          varchar(32)  not null
);
