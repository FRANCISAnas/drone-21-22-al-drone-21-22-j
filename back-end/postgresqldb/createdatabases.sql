CREATE
USER patrick WITH PASSWORD 'mr_roboto446';

CREATE
DATABASE jolydroneflightmonitordb;
GRANT ALL PRIVILEGES ON DATABASE
jolydroneflightmonitordb TO patrick;

CREATE
DATABASE jolydronedeliveryplannerdb;
GRANT ALL PRIVILEGES ON DATABASE
jolydronedeliveryplannerdb TO patrick;

CREATE
DATABASE jolydronecustomerdb;
GRANT ALL PRIVILEGES ON DATABASE
jolydronecustomerdb TO patrick;

CREATE
DATABASE jolydronestationmanagerdb;
GRANT ALL PRIVILEGES ON DATABASE
jolydronestationmanagerdb TO patrick;
