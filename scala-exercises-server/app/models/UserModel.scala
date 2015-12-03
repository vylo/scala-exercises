package models

import scala.concurrent.Future
import shared.User
import doobie.imports._

trait UserRepository {

  def getByLogin(login: String): ConnectionIO[Option[User]]

  def create(
      login: String,
      name: String,
      github_id: String,
      picture_url: String,
      github_url: String,
      email: String): ConnectionIO[User]

}

object UserRepository {

  implicit def userRepository : UserRepository = new UserSlickRepository

}

//Postgre Version
class UserSlickRepository extends UserRepository {

  override def getByLogin(login: String): ConnectionIO[Option[User]] =
    sql"select id, login, name, github_id, picture_url, github_url, email from users where login = $login".query[User].option

  override def create(
      login: String,
      name: String,
      github_id: String,
      picture_url: String,
      github_url: String,
      email: String): ConnectionIO[User] = {

    sql"insert into users (login, name, github_id, picture_url, github_url, email) values ($login, $name, $github_id, $picture_url, $github_url, $email)"
        .update.withUniqueGeneratedKeys[User](login, name, github_id, picture_url, github_url, email)
  }

}

