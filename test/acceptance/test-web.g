@prefix u   "http://www.genyris.org/lang/utilities#"
@prefix web "http://www.genyris.org/lang/web#"

def prepend-home (relative-path) (System!HOME (.+ '/' relative-path))

var threads
    list
        httpd 7778 (prepend-home "test/mocks/www-text.g") "."
        httpd 7779 (prepend-home "test/mocks/www-static.g") "."
        httpd 7780 (prepend-home "test/mocks/www-post.g") "."
sleep 1000    

def test-web-get()
    var response
        web:get 'http://127.0.0.1:7778/?A=1&B=2' 
    var receivedData
        response(.readAll)         
    assertEqual receivedData '~ "hello world"'


def test-web-static-get()
    var result (web:get "http://localhost:7779/LICENSE")
    var sum (result(.digest "MD5"))
    assertEqual sum "73ddde084d8b0dfc11ef415f14ba2cb0"


def test-web-post()
    var response
        web:post 'http://127.0.0.1:7780/' 
            data
                a = 'test-web-post'
                x = 908
            data
                'authorization' = 'Basic Zm9vOmJhcg=='
    var receivedData
        response(.readAll)
    assertEqual receivedData "('a' = 'test-web-post') ('x' = '908')" 

test-web-get
test-web-static-get
test-web-post

for thread in threads
    thread(.kill)


