From: 

如果一个SIP消息中没有Contact或者Record-Route头域，那么callee就会根据From头域产生后续的Request。比如：如果 Alice打一个电话给Bob，From头域的内容是 From:Alice<sip:alice@example.org>。那么Bob打给Alice时就会使用 sip:alice@example.org作为To头域和Request-URI头域的内容。 

Contact: 

后续Request将根据Contact头域的内容决定目的地的地址，同时将Contact头域的内容放到Request-URI中。它还可以用来指示没 有在Record-Route头域中记录的Proxies的地址。同时它还可以被用在Redirect servers和REGISTER requests 和responses。 

Record-Route/Route: 

Record-Route头域一般是被proxies插入到request中的，这样后续的Request如何有着和前面一样的call-id就会被路由 到这些proxies。它也会被User Agent作为发送后续request的依据。这套机制很像source-route，Record-Route头域的信息被复制到Route头域中。并 且Request-URI头域会被设置为第一个Route头域的内容。 

Via: 

Via头域是被服务器插入request中，用来检查路由环的，并且可以使response根据via找到返回的路。它不会对未来的request 或者是response造成影响。 

总的来说，如果有Route，request就应该根据Route发送，如果没有就根据Contact头域发送，如果连Contact都没有，就根据From头域发送。 
