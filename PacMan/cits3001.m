M = dlmread('data.txt');
N = sortrows(M);
disp(N(:,1));
s = size(N);
%P = zeros(s(1)/5, s(2));
%for i = 1:5:s(1)
%    average = N(i,3);
%    average = average + N(i+1,3);
 %   average = average + N(i+2,3);
 %   average = average + N(i+3,3);
 %   average = average + N(i+4,3);
 %   P(round(i/5)+1,1) = N(i,1);
 %   P(round(i/5)+1,2) = N(i,2);
 %   P(round(i/5)+1,3) = average/5;
%end
x = M(:, 1);
y = M(:, 2);
n = 25; % order of the fitted polynomial trendline
[p,s,mu] = polyfit(x, y, n);
m = 1000; % number of trendline points (the larger the smoother)
xx = linspace(min(x), max(x), m);
yy = polyval(p, (xx-mu(1))/mu(2));

figure;
hold on;
scatter(M(:,1), M(:,2));
plot(xx, yy, 'r-');
xlabel('a1');
ylabel('score');
[a,b] = min(yy);
disp(b);
stem(b, a);