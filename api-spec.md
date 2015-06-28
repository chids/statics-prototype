# Statics

## Concepts

Name | Description
----------|------------
domain		  | The domain part of a document location
type    | The type part of a document location
id   | The id part of a document location
location | The full path to a document: `/{domain}/{type}/{id}`
revision | MD5 hash of the current content at the given `location`

## Generic errors

* `HTTP 400` if the request path isn't made up of `{domain}/{type}/{id}`
* `HTTP 412` if the `If-Match` header is required but missing

## Create

Creates a new document with an initial revision.

`POST /{domain}/{type}/{id}`

> `POST /omni/article/D7BA6377-CA73-41AB-A6D6-046D97506385`

### Returns

If the request succeeded, then `HTTP 201` is returned with the submitted document in the response body.

Header   | Description
---------|---------
Etag     | `{revision}` of the created document
Location | URL to the newly created revision

Errors:
* `HTTP 304` if the request matches the document's current revision
* `HTTP 409` if the document didn't exist in the cache, storage or with another current revision

## Update

Creates a new revision of an existing document.

`PUT /{domain}/{type}/{id}`

> `PUT /omni/article/D7BA6377-CA73-41AB-A6D6-046D97506385`

Header   | Description
---------|---------
If-Match | `{revision}`

### Returns

If the request succeeded, then `HTTP 202` is returned with tag in the response body.

Header   | Description
---------|---------
Etag     | `{revision}`
Location | URL to the newly created revision

Errors:
* `HTTP 304` if the request matches the document's current revision
* `HTTP 409` if the document didn't exist in the cache, storage or with another current revision

## Delete

Deletes a document

`DELETE {domain}/{type}/{id}`

> `DELETE /omni/article/D7BA6377-CA73-41AB-A6D6-046D97506385`

Header   | Description
---------|---------
If-Match | `{revision}`

### Returns

If the request succeeded, then `HTTP 204` is returned with the  in the response body.

Errors:
* `HTTP 412` if the request doesn't match the document's current revision
