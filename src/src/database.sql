PRAGMA foreign_keys=OFF;

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS journals;
DROP TABLE IF EXISTS logs;

PRAGMA foreign_keys=ON;

CREATE TABLE users(
  username TEXT PRIMARY KEY,
  fullname TEXT,
  role TEXT,
  division TEXT,
  password TEXT,
  salt TEXT
);

CREATE TABLE journals(
  id TEXT DEFAULT (lower(hex(randomblob(16)))),
  patient TEXT,
  doctor TEXT,
  nurse TEXT,
  content TEXT,
  division TEXT,

  PRIMARY KEY(id)
  FOREIGN KEY(patient) REFERENCES users(username)
  FOREIGN KEY(doctor) REFERENCES users(username)
  FOREIGN KEY(nurse) REFERENCES users(username)
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
  FOREIGN KEY(user) REFERENCES users(username)
);
