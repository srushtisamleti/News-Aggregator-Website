document.addEventListener('DOMContentLoaded', function() {
    console.log('App initialized.');

    fetch('/articles/fetchAll')
        .then(response => response.json())
        .then(data => {
            const newsList = document.getElementById('news-list');
            newsList.innerHTML = '';

            data.forEach(article => {
                const listItem = document.createElement('li');
                listItem.innerHTML = `
                    <h3>${article.title}</h3>
                    <p>${article.content}</p>
                    <p>Published on: ${new Date(article.publishedDate).toLocaleDateString()}</p>
                `;
                newsList.appendChild(listItem);
            });
        })
        .catch(error => {
            console.error('Error fetching articles:', error);
        });
});
