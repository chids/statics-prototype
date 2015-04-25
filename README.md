# Static

Immutable (static) data. Characteristics: _expensive to produce, cheap to consume_.

## About

Static is Heroku friendly "port" of [Pinterest's Config v2](http://engineering.pinterest.com/post/112895488589/serving-configuration-data-at-scale-with-high)
and as such it is a service for read intensive rather than write intensive workloads.

### Differences from Pinterest's Config v2

* Redis instead of Zookeeper for coordination
* Redis instead of Zookeeper for broadcasting changes
* Uses AWS S3 webpage redirects to expose the latest revision of each document at a "known URL"

### Storage

* General layout
   * `[domain][separator][type][separator][id][separator][qualifier=[revision]|current]`
* S3
   * `[domain]/[type]/[id]/[revision]`
   * `[domain]/[type]/[id]/current -> [revision]` (using [AWS S3 webpage redirect](http://docs.aws.amazon.com/AmazonS3/latest/dev/how-to-page-redirect.html))
* Redis:
   * `[domain]:[type]:[id]=[revision]`
   * `lock:[domain]:[type]:[id]` (written with expiration)

<a href="https://docs.google.com/a/schibsted.com/drawings/d/1jUydKGu3_ta4VJ6PiDPwQbztHaZXOGECYEuuTPFURYs/edit"><img src="https://docs.google.com/drawings/d/1jUydKGu3_ta4VJ6PiDPwQbztHaZXOGECYEuuTPFURYs/pub?w=1171&amp;h=826"/></a>
