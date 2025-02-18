create table if not exists import_entity_data
(
    dbid  varchar(255) not null,
    id    varchar(255),
    label varchar(255),
    url   varchar(255),
    constraint import_entity_data_pkey
        primary key (dbid)
);

create table if not exists import_file
(
    id           varchar(255) not null,
    datei_format integer,
    datei_name   varchar(255),
    datei_typ    varchar(255),
    error        boolean      not null,
    message      varchar(4092),
    path         varchar(255),
    constraint import_file_pkey
        primary key (id)
);

create table if not exists import_file_import_entity_data
(
    import_file_id          varchar(255) not null,
    import_entity_data_dbid varchar(255) not null,
    constraint import_file_import_entity_data_pkey
        primary key (import_file_id, import_entity_data_dbid),
    constraint uk_jky66gbd2x8ye7qplpsoms575
        unique (import_entity_data_dbid),
    constraint fkaghtto0ms25jck8tb9poux6bv
        foreign key (import_file_id) references import_file,
    constraint fkq37i4g0wuq2egeih42dp9xn27
        foreign key (import_entity_data_dbid) references import_entity_data
);

create table if not exists import_job
(
    id            varchar(255) not null,
    benutzer_name varchar(255),
    creation_date timestamp,
    datatype      varchar(255),
    error_message text,
    import_dir    varchar(255),
    name          varchar(255),
    result        integer,
    constraint import_job_pkey
        primary key (id)
);

create table if not exists import_job_import_files
(
    import_job_id   varchar(255) not null,
    import_files_id varchar(255) not null,
    constraint import_job_import_files_pkey
        primary key (import_job_id, import_files_id),
    constraint uk_tmyyss0wwh8p5ix7si5e5ekfy
        unique (import_files_id),
    constraint fkn1e1g9tl697cyuujvacvwwgwc
        foreign key (import_job_id) references import_job,
    constraint fkf5vmbhndhgqiry3xh3uqrjely
        foreign key (import_files_id) references import_file
);
