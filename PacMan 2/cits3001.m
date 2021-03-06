M = dlmread('data.txt');
N = sortrows(M,2);
O = sortrows(M,2);
disp(O(:,2));

x = O(:, 1);
y = O(:, 2);
n = 10; % order of the fitted polynomial trendline
f = fit( [x, y], O(:, 3), 'poly23' );
m = 1000; % number of trendline points (the larger the smoother)
%xx = linspace(min(x), max(x), m);
%yy = polyval(p, (xx-mu(1))/mu(2));

figure;
%plot(f, [x,y], O(:, 3));
plot(f, [x,y], O(:, 3), 'Style', 'contour');
xlabel('a1')
ylabel('a2')
zlabel('Score')
%colormap('hsv');
colorbar()
%scatter3(O(:, 1), O(:, 2), O(:, 3));
%plot(xx, yy, 'r-');
%xlabel('a1');
%ylabel('score');