package app.model

import java.time.OffsetDateTime

import argonaut.Argonaut._
import argonaut.{CodecJson, DecodeJson, EncodeJson}

import scala.util.Try

object Codecs {

  implicit def DateCodec: CodecJson[OffsetDateTime] = CodecJson.derived(
    EncodeJson[OffsetDateTime](d => jString(d.toString)),
    DecodeJson.optionDecoder(_.string flatMap (dateString => Try(OffsetDateTime.parse(dateString)).toOption),
      "Unable to parse date, it must be in ISO8601 format")
  )

  implicit def EventIdCodec: CodecJson[EventId] = WrapperCodec(EventId, _.id)
  implicit def EntityIdCodec: CodecJson[EntityId] = WrapperCodec(EntityId, _.id)
  implicit def SnapshotIdCodec: CodecJson[SnapshotId] = WrapperCodec(SnapshotId, _.id)
  implicit def UriCodec: CodecJson[URI] = WrapperCodec(URI, _.url)
  implicit def SystemNameCodec: CodecJson[SystemName] = WrapperCodec(SystemName, _.name)

  implicit def WrapperCodec[A](decode: String => A, encode: A => String): CodecJson[A] =
    CodecJson.derived(
      EncodeJson(e => jString(encode(e))),
      DecodeJson(c => c.as[String] map decode))


  implicit def ReceivedEventCodec: CodecJson[ReceivedEvent] =
    casecodec4(ReceivedEvent.apply, ReceivedEvent.unapply)("entityId", "systemName", "timestamp", "body")

  implicit def EventCodec: CodecJson[Event] =
    casecodec6(Event.apply, Event.unapply)("id", "entityId",
      "systemName", "createdTimestamp", "suppliedTimestamp", "body")

  implicit def SnapshotEncoder: EncodeJson[Snapshot] =
    jencode5L(Snapshot.unapply _ andThen (_.get))("id", "entityId", "systemName", "timestamp", "body")

  implicit def LinksEncoder: EncodeJson[Links] =
    jencode4L(Links.unapply _ andThen (_.get))("self", "first", "next", "prev")

  implicit def LinkedResponseEncoder: EncodeJson[LinkedResponse] =
    jencode4L(LinkedResponse.unapply _ andThen (_.get))("events", "pageNumber", "pageSize", "_links")

  implicit def EntityCodec: EncodeJson[Entity] = jencode3L(Entity.unapply _ andThen(_.get))("entityId", "body", "systemName")


}
