# Statics

Immutable (static) data. Characteristics: _expensive to produce, cheap and reliable to consume_.

## About

Statics is Heroku friendly "port" of [Pinterest's Config v2](http://engineering.pinterest.com/post/112895488589/serving-configuration-data-at-scale-with-high)
and as such it is a service for read intensive rather than write intensive workloads.

### Differences from Pinterest's Config v2

* Redis instead of Zookeeper
   * (for coordination and broadcasting changes)
* Expose the latest revision of each document at a "known URL"
   * (using AWS S3 webpage redirects)
* Stores any type of content
   * (binary data + mime type)

### Similarities to Pinterest's Config v2

* Immutable append only storage
  * (to benefit from S3's "read-after-write" consistency for new files)
* New versions are written under a per document write lock
  * (managed as a key in Redis)

<img src="design.svg"/>

### Storage

* General layout
   * `[domain][separator][type][separator][id][separator][qualifier=[revision]|current]`
* S3
   * `[domain]/[type]/[id]/[revision]`
   * `[domain]/[type]/[id]/current -> [revision]`
      * (using [AWS S3 webpage redirect](http://docs.aws.amazon.com/AmazonS3/latest/dev/how-to-page-redirect.html))
* Redis:
   * `[domain]:[type]:[id]=[revision]`
   * `lock:[domain]:[type]:[id]`
      * (written with expiration)
