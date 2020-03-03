PRAGMA foreign_keys=OFF;

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS journals;
DROP TABLE IF EXISTS logs;

PRAGMA foreign_keys=ON;

CREATE TABLE users(
  username TEXT,
  ssn INT PRIMARY KEY,
  fullname TEXT,
  role TEXT,
  division TEXT,
  password TEXT,
  salt TEXT
);

CREATE TABLE journals(
  id TEXT DEFAULT (lower(hex(randomblob(16)))),
  patientssn TEXT,
  doctorssn TEXT,
  nursessn TEXT,
  content TEXT,
  division TEXT,

  PRIMARY KEY(id)
  FOREIGN KEY(patientssn) REFERENCES users(ssn)
  FOREIGN KEY(doctorssn) REFERENCES users(ssn)
  FOREIGN KEY(nursessn) REFERENCES users(ssn)
);

CREATE TABLE logs(
  id TEXT DEFAULT (lower(hex(randomblob(16)))),
  journal TEXT,
  access_date DATE,
  access_time TIME,
  user TEXT,
  action TEXT,

  PRIMARY KEY(id)
  FOREIGN KEY(journal) REFERENCES journals(id)
  FOREIGN KEY(user) REFERENCES users(ssn)
);


INSERT
INTO users(username, fullname, role, password)
VALUES ("ss", "socialstyrelsen", "gov", "password"),
       ("admin", "admin", "admin", "password");
