#!/home/birchb/workspace/genyris/bin/genyris

@prefix u   "http://www.genyris.org/lang/utilities#"
@prefix web "http://www.genyris.org/lang/web#"

def prepend-home (relative-path) (System!HOME (.+ '/' relative-path))

var threads
    list
        httpd 7778 (prepend-home "test/mocks/www-text.g") "."
        httpd 7779 (prepend-home "test/mocks/www-static.g") "."
        httpd 7780 (prepend-home "test/mocks/www-post.g") "."
        httpd 7781 (prepend-home "test/mocks/www-text-401.g") "."
sleep 1000    

def test-web-get-not-200()
    var response
        web:get 'http://127.0.0.1:7781/?A=1&B=2'
    assert response
    assert (equal? (left (nth 2 response)) 401)
    var receivedData
        (left response)(.readAll)
    var headers (tag Alist (nth 1 response))          
    assertEqual '15' (headers(.lookup 'Content-Length'))
    assertEqual receivedData '~ "hello world"'

def test-web-get()
    var response
        web:get 'http://127.0.0.1:7778/?A=1&B=2'
    assert response
    assert (equal? (left (nth 2 response)) 200)
    var receivedData
        (left response)(.readAll)
    var headers (tag Alist (nth 1 response))          
    assertEqual '15' (headers(.lookup 'Content-Length'))
    assertEqual receivedData '~ "hello world"'

def test-web-get-1()
    var response
        web:get 'http://127.0.0.1:7778/?A=1&B=2' nil nil ^insecure
    assert response
    assert (equal? (left (nth 2 response)) 200)
    var receivedData
        (left response)(.readAll)
    var headers (tag Alist (nth 1 response))          
    assertEqual '15' (headers(.lookup 'Content-Length'))
    assertEqual receivedData '~ "hello world"'


def test-web-static-get()
    var result (web:get "http://localhost:7779/LICENSE" nil '1.1')
    assert result
    assert (equal? (left (nth 2 result)) 200)
    var headers (tag Alist (nth 1 result))          
    assertEqual '1559' (headers(.lookup 'Content-Length'))
    var sum ((left result)(.digest "MD5"))
    assertEqual sum "73ddde084d8b0dfc11ef415f14ba2cb0"


def test-web-post()
    var response
        web:post 'http://127.0.0.1:7780/' 
            data
                a = 'test-web-post'
                x = 908
            data
                'authorization' = 'Basic Zm9vOmJhcg=='
    assert response
    assert (equal? (left (nth 2 response)) 200)
    var receivedData
        (left response)
            .readAll
    var headers (tag Alist (nth 1 response))          
    assertEqual '37' (headers(.lookup 'Content-Length'))
    assertEqual receivedData "('a' = 'test-web-post') ('x' = '908')" 

def test-web-post-1()
    var response
        web:post 'http://127.0.0.1:7780/'
            data
                a = 'test-web-post'
                x = 908
            data
                'authorization' = 'Basic Zm9vOmJhcg=='
            ~ '1.0'
            ~ ^insecure
    assert response
    assert (equal? (left (nth 2 response)) 200)
    var receivedData
        (left response)
            .readAll
    var headers (tag Alist (nth 1 response))          
    assertEqual '37' (headers(.lookup 'Content-Length'))
    assertEqual receivedData "('a' = 'test-web-post') ('x' = '908')" 

test-web-get
test-web-get-1
test-web-static-get
test-web-post
test-web-post-1

for thread in threads
    thread(.kill)


