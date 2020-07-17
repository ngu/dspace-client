# A Java Dspace Client
A java-library for working with a remote DSpace instance using RESTful interface

This library is intended for use with DSpace 6.x, but might also work with other versions with minor tweaking
For DSpace v5, the login-methods are different.

## Known limitations
- A known limitation of DSpace v6 REST-api is that you cannot HTTP PUT (e.g. Update) an Item. 
Q: What does this mean in practice? 
A: If you want to programmatically withdraw an Item, this is not possible using dspace-rest.
   You must open the Item in xmlui, Edit Item, Click Withdraw and then confirm Withdraw.
   
Of course, you could always use the delete-method to delete an Item, but if there are harvesters connected to your
DSpace instance, they won't get notice that the Item has gone if you use delete rather than Withdraw.
In DSpace v7, there's been a massive rewrite of the REST-api - and hopefully Http Put on Items will finally work.

### Inspiration
https://github.com/BrunoNZ/dspace-rest-requests
